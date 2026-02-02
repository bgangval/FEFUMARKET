package com.example.fefumarket.data.models

// Модель данных для элемента избранного товара,
// используется для отображения списка избранных товаров и передачи данных между слоями
data class FavoriteItem(
    val title: String,
    val description: String,
    val imageResId: Int
)