from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime


class ProductBase(BaseModel):
    title: str
    price: float
    category: str
    condition: str
    building: str
    description: Optional[str] = None


class ProductCreate(ProductBase):
    pass


class ProductUpdate(BaseModel):
    title: Optional[str] = None
    price: Optional[float] = None
    category: Optional[str] = None
    condition: Optional[str] = None
    building: Optional[str] = None
    description: Optional[str] = None


class ProductOut(ProductBase):
    id: int
    owner_id: int
    created_at: datetime

    class Config:
        from_attributes = True


# 📄 LIST RESPONSE WITH PAGINATION
class ProductListOut(BaseModel):
    items: List[ProductOut]
    total: int
    page: int
    page_size: int
