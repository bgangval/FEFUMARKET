from pydantic import BaseModel
from datetime import datetime
from typing import List
from schemas.messages import MessageOut

class ChatOut(BaseModel):
    id: int
    product_id: int
    buyer_id: int
    seller_id: int
    created_at: datetime
    messages: List[MessageOut] = []

    class Config:
        from_attributes = True
