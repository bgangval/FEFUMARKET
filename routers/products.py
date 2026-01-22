from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import Optional

from database import get_db
from models.products import Product
from models.users import User
from schemas.products import (
    ProductCreate,
    ProductUpdate,
    ProductOut,
    ProductListOut,
)
from core.security import get_current_user
from dependencies.pagination import pagination_params

router = APIRouter(prefix="/products", tags=["Products"])


# 🔐 CREATE product — AUTH REQUIRED
@router.post("/", response_model=ProductOut)
def create_product(
    data: ProductCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = Product(
        **data.model_dump(),
        owner_id=current_user.id,
    )
    db.add(product)
    db.commit()
    db.refresh(product)
    return product


# 🌍 GET products — PUBLIC
# search + filters + pagination + sorting
@router.get("/", response_model=ProductListOut)
def get_products(
    db: Session = Depends(get_db),
    pagination: dict = Depends(pagination_params),

    # search / filters
    search: Optional[str] = None,
    category: Optional[str] = None,
    condition: Optional[str] = None,
    building: Optional[str] = None,
    min_price: Optional[float] = None,
    max_price: Optional[float] = None,

    # sorting
    sort_by: str = Query("created_at", pattern="^(created_at|price)$"),
    order: str = Query("desc", pattern="^(asc|desc)$"),
):
    query = db.query(Product).filter(Product.is_active == True)

    if search:
        query = query.filter(
            Product.title.ilike(f"%{search}%") |
            Product.description.ilike(f"%{search}%")
        )

    if category:
        query = query.filter(Product.category == category)

    if condition:
        query = query.filter(Product.condition == condition)

    if building:
        query = query.filter(Product.building == building)

    if min_price is not None:
        query = query.filter(Product.price >= min_price)

    if max_price is not None:
        query = query.filter(Product.price <= max_price)

    # total BEFORE pagination
    total = query.count()

    # sorting
    sort_column = getattr(Product, sort_by)
    if order == "desc":
        sort_column = sort_column.desc()

    items = (
        query
        .order_by(sort_column)
        .offset(pagination["offset"])
        .limit(pagination["page_size"])
        .all()
    )

    return {
        "items": items,
        "total": total,
        "page": pagination["page"],
        "page_size": pagination["page_size"],
    }


# 🌍 GET single product — PUBLIC
@router.get("/{product_id}", response_model=ProductOut)
def get_product(
    product_id: int,
    db: Session = Depends(get_db),
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(404, "Product not found")
    return product


# 🔐 UPDATE product — OWNER ONLY
@router.put("/{product_id}", response_model=ProductOut)
def update_product(
    product_id: int,
    data: ProductUpdate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(404, "Product not found")

    if product.owner_id != current_user.id:
        raise HTTPException(403, "Forbidden")

    for key, value in data.model_dump(exclude_unset=True).items():
        setattr(product, key, value)

    db.commit()
    db.refresh(product)
    return product


# 🔐 DELETE product — OWNER ONLY
@router.delete("/{product_id}")
def delete_product(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(404, "Product not found")

    if product.owner_id != current_user.id:
        raise HTTPException(403, "Forbidden")

    db.delete(product)
    db.commit()
    return {"message": "Product deleted"}
