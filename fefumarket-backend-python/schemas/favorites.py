from pydantic import BaseModel

class FavoriteOut(BaseModel):
    product_id: int

    class Config:
        from_attributes = True
