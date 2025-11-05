package com.example.fefumarket

data class Ad(
    val title: String,        // Название товара
    val price: String,        // Цена
    val dorm: String,         // Корпус общежития
    val seller: String,       // Имя продавца
    val description: String,  // Описание
    val imageResId: Int       // Ресурс изображения
)