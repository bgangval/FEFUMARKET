from sqlalchemy import Column, Integer, String, Boolean, DateTime
from datetime import datetime
from database import Base
from sqlalchemy.orm import relationship



class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    email = Column(String, unique=True, index=True, nullable=False)
    password_hash = Column(String, nullable=False)
    name = Column(String, nullable=False)
    avatar_url = Column(String, nullable=True)
    is_verified = Column(Boolean, default=False)
    created_at = Column(DateTime, default=datetime.utcnow)
    favorites = relationship("Favorite", backref="user", cascade="all, delete")
    chats_as_buyer = relationship("Chat", foreign_keys="Chat.buyer_id")
    chats_as_seller = relationship("Chat", foreign_keys="Chat.seller_id")
    is_admin = Column(Boolean, default=False, nullable=False)