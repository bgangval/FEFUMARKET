from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List

from database import get_db
from core.security import get_current_user
from models.favorites import Favorite
from models.products import Product
from models.users import User
from schemas.products import ProductOut

router = APIRouter(prefix="/favorites", tags=["Favorites"])


# ⭐ добавить в избранное
@router.post("/{product_id}")
def add_favorite(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    exists = (
        db.query(Favorite)
        .filter(
            Favorite.user_id == current_user.id,
            Favorite.product_id == product_id,
        )
        .first()
    )
    if exists:
        raise HTTPException(status_code=400, detail="Already in favorites")

    fav = Favorite(user_id=current_user.id, product_id=product_id)
    db.add(fav)
    db.commit()

    return {"message": "Added to favorites"}


# 📄 список избранных
@router.get("/", response_model=List[ProductOut])
def get_favorites(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    products = (
        db.query(Product)
        .join(Favorite, Favorite.product_id == Product.id)
        .filter(Favorite.user_id == current_user.id)
        .all()
    )
    return products


# 🗑 удалить из избранного
@router.delete("/{product_id}")
def remove_favorite(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    fav = (
        db.query(Favorite)
        .filter(
            Favorite.user_id == current_user.id,
            Favorite.product_id == product_id,
        )
        .first()
    )
    if not fav:
        raise HTTPException(status_code=404, detail="Not in favorites")

    db.delete(fav)
    db.commit()

    return {"message": "Removed from favorites"}
