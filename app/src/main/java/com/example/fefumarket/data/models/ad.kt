package com.example.fefumarket.data.models

// Внутренняя модель объявления, используемая в приложении для отображения,
// редактирования и передачи данных между слоями (UI, ViewModel, API)
data class Ad(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val price: String,
    val dorm: String,
    val seller: String,
    val description: String,
    val category: String,
    val condition: String,
    val imageUris: List<String> = listOf(),
    var isSold: Boolean = false
)