from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from datetime import datetime

from database import get_db
from models.users import User

from schemas.auth import (
    UserRegister,
    UserLogin,
    Token,
    )

from core.security import (
    hash_password,
    verify_password,
    create_access_token,
)


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
        name=data.name,
        is_admin=False,
    )

    db.add(user)
    db.commit()
    db.refresh(user)


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
