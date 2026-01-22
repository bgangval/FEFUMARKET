import random

def generate_code() -> str:
    return f"{random.randint(1000, 9999)}"
