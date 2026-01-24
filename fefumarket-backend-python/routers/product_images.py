from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session

from database import get_db
from core.security import get_current_user
from models.products import Product
from models.product_images import ProductImage
from models.users import User
from schemas.product_images import ProductImageOut

router = APIRouter(
    prefix="/products",
    tags=["Product Images"]
)


@router.post("/{product_id}/images", response_model=ProductImageOut)
def add_image(
    product_id: int,
    image_url: str,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user),
):
    # продукт существует?
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")

    # только владелец может добавлять фото
    if product.owner_id != current_user.id:
        raise HTTPException(status_code=403, detail="Forbidden")

    # максимум 3 фото
    if len(product.images) >= 3:
        raise HTTPException(status_code=400, detail="Maximum 3 images")

    image = ProductImage(
        image_url=image_url,
        product_id=product_id
    )

    db.add(image)
    db.commit()
    db.refresh(image)

    return image
