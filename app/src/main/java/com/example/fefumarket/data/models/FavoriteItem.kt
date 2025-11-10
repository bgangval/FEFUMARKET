package com.example.fefumarket.data.models

// Модель данных для избранного товара
data class FavoriteItem(
    val title: String,       // Название товара
    val description: String, // Описание или цена
    val imageResId: Int      // Ресурс изображения
)