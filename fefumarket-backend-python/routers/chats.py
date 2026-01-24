from fastapi import APIRouter, Depends, HTTPException
from sqlalchemy.orm import Session
from typing import List

from database import get_db
from core.security import get_current_user
from models.users import User
from models.products import Product
from models.chats import Chat
from models.messages import Message
from schemas.chats import ChatOut
from schemas.messages import MessageCreate, MessageOut

router = APIRouter(prefix="/chats", tags=["Chats"])


# ✅ создать или получить чат по объявлению
@router.post("/{product_id}", response_model=ChatOut)
def get_or_create_chat(
    product_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    product = db.query(Product).filter(Product.id == product_id).first()
    if not product:
        raise HTTPException(404, "Product not found")

    if product.owner_id == current_user.id:
        raise HTTPException(400, "Cannot chat with yourself")

    chat = (
        db.query(Chat)
        .filter(
            Chat.product_id == product_id,
            Chat.buyer_id == current_user.id,
            Chat.seller_id == product.owner_id
        )
        .first()
    )

    if not chat:
        chat = Chat(
            product_id=product_id,
            buyer_id=current_user.id,
            seller_id=product.owner_id
        )
        db.add(chat)
        db.commit()
        db.refresh(chat)

    return chat


# ✅ список чатов пользователя
@router.get("/", response_model=List[ChatOut])
def get_my_chats(
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    return (
        db.query(Chat)
        .filter(
            (Chat.buyer_id == current_user.id) |
            (Chat.seller_id == current_user.id)
        )
        .order_by(Chat.created_at.desc())
        .all()
    )


# ✅ НОВОЕ: получить конкретный чат + сообщения
@router.get("/{chat_id}", response_model=ChatOut)
def get_chat(
    chat_id: int,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    chat = db.query(Chat).filter(Chat.id == chat_id).first()
    if not chat:
        raise HTTPException(404, "Chat not found")

    if current_user.id not in [chat.buyer_id, chat.seller_id]:
        raise HTTPException(403, "Forbidden")

    return chat


# ✅ отправить сообщение
@router.post("/{chat_id}/messages", response_model=MessageOut)
def send_message(
    chat_id: int,
    data: MessageCreate,
    db: Session = Depends(get_db),
    current_user: User = Depends(get_current_user)
):
    chat = db.query(Chat).filter(Chat.id == chat_id).first()
    if not chat:
        raise HTTPException(404, "Chat not found")

    if current_user.id not in [chat.buyer_id, chat.seller_id]:
        raise HTTPException(403, "Forbidden")

    message = Message(
        chat_id=chat_id,
        sender_id=current_user.id,
        text=data.text
    )

    db.add(message)
    db.commit()
    db.refresh(message)

    return message

