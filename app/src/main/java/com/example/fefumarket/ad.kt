package com.example.fefumarket

import java.util.concurrent.locks.Condition
import android.net.Uri

data class Ad(
    val id: String = System.currentTimeMillis().toString(),  // Уникальный идентификатор
    val title: String,        // Название товара
    val price: String,        // Цена
    val dorm: String,         // Корпус общежития
    val seller: String,       // Имя продавца
    val description: String,  // Описание
    val category: String,     // Категория
    val condition: String,    // Состояние
    val imageUris: List<String> = listOf()  // Ресурс изображения
)