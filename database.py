from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base
from dotenv import load_dotenv
from core.config import DATABASE_URL
import os

# Загружаем переменные из .env
load_dotenv()

# Берём строку подключения к БД
DATABASE_URL = os.getenv("DATABASE_URL")

# На случай, если .env не подхватился
if DATABASE_URL is None:
    raise ValueError("DATABASE_URL is not set. Check your .env file")

# Создаём движок SQLAlchemy
engine = create_engine(DATABASE_URL)

# Сессии для работы с БД
SessionLocal = sessionmaker(
    autocommit=False,
    autoflush=False,
    bind=engine
)

# Базовый класс для всех моделей
Base = declarative_base()

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Dependency для FastAPI
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
