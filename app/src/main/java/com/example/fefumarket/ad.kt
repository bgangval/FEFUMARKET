package com.example.fefumarket

import java.util.concurrent.locks.Condition

data class Ad(
    val title: String,        // Название товара
    val price: String,        // Цена
    val dorm: String,         // Корпус общежития
    val seller: String,       // Имя продавца
    val description: String,  // Описание
    val category: String,     // Категория
    val condition: String,
    val imageResId: Int       // Ресурс изображения
)