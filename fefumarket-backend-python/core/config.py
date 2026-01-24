from dotenv import load_dotenv
import os

load_dotenv()

ENV = os.getenv("ENV", "dev")

SECRET_KEY = os.getenv("SECRET_KEY")
ACCESS_TOKEN_EXPIRE_MINUTES = int(
    os.getenv("ACCESS_TOKEN_EXPIRE_MINUTES", 60 * 24)
)

DATABASE_URL = os.getenv("DATABASE_URL")

if not SECRET_KEY:
    raise RuntimeError("SECRET_KEY is not set")

if not DATABASE_URL:
    raise RuntimeError("DATABASE_URL is not set")
