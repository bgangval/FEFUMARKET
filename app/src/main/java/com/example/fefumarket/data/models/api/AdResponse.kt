package com.example.fefumarket.data.models.api

data class AdResponse(
    val id: Int,
    val title: String,
    val description: String,
    val price: Int,
    val owner_id: Int,
    val created_at: String
)