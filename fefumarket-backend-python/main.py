from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from routers import auth
from routers import users
from routers import products
from routers import categories
from routers import product_images
from routers import favorites
from routers import chats
from dotenv import load_dotenv

load_dotenv()

app = FastAPI()

# ================= CORS =================
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # потом сузим до фронта
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# ================= ROUTERS =================
app.include_router(auth.router)
app.include_router(users.router)
app.include_router(categories.router)
app.include_router(products.router)
app.include_router(product_images.router)
app.include_router(favorites.router)
app.include_router(chats.router)

# ================= ROOT =================
@app.get("/")
def root():
    return {"message": "Backend is running"}
