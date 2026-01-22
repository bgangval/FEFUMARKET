from pydantic import BaseModel, EmailStr

class UserOut(BaseModel):
    id: int
    email: str
    name: str
    avatar_url: str | None
    is_admin: bool

    class Config:
        from_attributes = True


class UserUpdate(BaseModel):
    name: str | None = None
    avatar_url: str | None = None
