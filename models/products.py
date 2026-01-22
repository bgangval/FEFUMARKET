from sqlalchemy import Column, Integer, String, Text, Float, Boolean, DateTime, ForeignKey
from sqlalchemy.orm import relationship
from datetime import datetime
from database import Base

class Product(Base):
    __tablename__ = "products"

    id = Column(Integer, primary_key=True, index=True)
    title = Column(String, nullable=False)
    description = Column(Text, nullable=True)
    price = Column(Float, nullable=False)

    category = Column(String, nullable=False)
    condition = Column(String, nullable=False)
    building = Column(String, nullable=False)

    is_active = Column(Boolean, default=True)
    owner_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"))
    created_at = Column(DateTime, default=datetime.utcnow)

    owner = relationship("User")
    favorites = relationship("Favorite", backref="product", cascade="all, delete")
    images = relationship(
        "ProductImage",
        back_populates="product",
        cascade="all, delete",
        passive_deletes=True
    )
