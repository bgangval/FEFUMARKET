from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import datetime

from database import get_db
from models.users import User
from models.email_verifications import EmailVerification

from schemas.auth import (
    UserRegister,
    UserLogin,
    Token,
    EmailVerifyIn,
)

from core.security import (
    hash_password,
    verify_password,
    create_access_token,
)

from services.email import send_verification_email
from utils.codes import generate_code

router = APIRouter(prefix="/auth", tags=["Auth"])


# =========================
# REGISTER
# =========================
@router.post("/register")
def register(
    data: UserRegister,
    db: Session = Depends(get_db),
):
    # проверка, что email не занят
    existing_user = db.query(User).filter(User.email == data.email).first()
    if existing_user:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Email already registered",
        )

    user = User(
        email=data.email,
        password_hash=hash_password(data.password),
        is_verified=False,
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    code = generate_code()

    verification = EmailVerification(
        user_id=user.id,
        code=code,
        expires_at=EmailVerification.expiry_time(),
    )

    db.add(verification)
    db.commit()

    send_verification_email(user.email, code)

    return {"message": "Verification code sent to email"}


# =========================
# VERIFY EMAIL
# =========================
@router.post("/verify-email")
def verify_email(
    data: EmailVerifyIn,
    db: Session = Depends(get_db),
):
    user = db.query(User).filter(User.email == data.email).first()
    if not user:
        raise HTTPException(status_code=404, detail="User not found")

    verification = (
        db.query(EmailVerification)
        .filter(
            EmailVerification.user_id == user.id,
            EmailVerification.code == data.code,
            EmailVerification.expires_at > datetime.utcnow(),
        )
        .first()
    )

    if not verification:
        raise HTTPException(
            status_code=400,
            detail="Invalid or expired code",
        )

    user.is_verified = True

    db.delete(verification)
    db.commit()

    return {"message": "Email successfully verified"}


# =========================
# LOGIN
# =========================
@router.post("/login", response_model=Token)
def login(
    data: UserLogin,
    db: Session = Depends(get_db),
):
    db_user = db.query(User).filter(User.email == data.email).first()

    if not db_user or not verify_password(
        data.password,
        db_user.password_hash,
    ):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Invalid credentials",
        )

    # 🔒 КЛЮЧЕВОЕ ПРАВИЛО
    if not db_user.is_verified:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Email is not verified",
        )

    token = create_access_token({"sub": str(db_user.id)})

    return {
        "access_token": token,
        "token_type": "bearer",
    }


# =========================
# LOGOUT
# =========================
@router.post("/logout")
def logout():
    # logout = удалить токен на фронте
    return {"message": "Logged out"}
