from pydantic import BaseModel

class ProductImageOut(BaseModel):
    id: int
    image_url: str

    class Config:
        from_attributes = True
