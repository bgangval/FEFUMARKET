package com.example.fefumarket.data.models

data class Ad(
    val id: String = System.currentTimeMillis().toString(),  // Уникальный идентификатор
    val title: String,        // Название товара
    val price: String,        // Цена
    val dorm: String,         // Корпус общежития
    val seller: String,       // Имя продавца
    val description: String,  // Описание
    val category: String,     // Категория
    val condition: String,    // Состояние
    val imageUris: List<String> = listOf(),
    var isSold: Boolean = false
)