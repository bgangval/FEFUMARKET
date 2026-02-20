package com.example.fefumarket.data.models.api

// Модель ответа сервера для чата
data class ChatOut(
    val id: Int,
    val product_id: Int,
    val buyer_id: Int,
    val seller_id: Int,
    val created_at: String,
    val messages: List<MessageOut> = emptyList()
)
