from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List

from database import get_db
from models.categories import Category
from schemas.categories import CategoryCreate, CategoryOut
from core.security import get_current_admin
from models.users import User

router = APIRouter(prefix="/categories", tags=["Categories"])


# 📄 публично — получить список категорий
@router.get("/", response_model=List[CategoryOut])
def get_categories(db: Session = Depends(get_db)):
    return db.query(Category).order_by(Category.name).all()


# 🔐 только админ — создать категорию
@router.post("/", response_model=CategoryOut)
def create_category(
    data: CategoryCreate,
    db: Session = Depends(get_db),
    current_admin: User = Depends(get_current_admin),
):
    exists = db.query(Category).filter(Category.name == data.name).first()
    if exists:
        raise HTTPException(status_code=400, detail="Category already exists")

    category = Category(**data.model_dump())
    db.add(category)
    db.commit()
    db.refresh(category)
    return category
