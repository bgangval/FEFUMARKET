from pydantic import BaseModel
from datetime import datetime

class MessageCreate(BaseModel):
    text: str

class MessageOut(BaseModel):
    id: int
    sender_id: int
    text: str
    created_at: datetime

    class Config:
        from_attributes = True
