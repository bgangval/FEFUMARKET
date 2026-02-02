package com.example.fefumarket.data.models

// Модель данных для объявления, используемая для отображения постов в приложении
data class Post(
    val id: Int,
    val title: String,
    val price: String,
    val imageUrl: String
)