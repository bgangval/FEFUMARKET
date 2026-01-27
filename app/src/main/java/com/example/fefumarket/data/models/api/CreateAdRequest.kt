package com.example.fefumarket.data.models.api

data class CreateAdRequest(
    val title: String,
    val description: String,
    val price: Int
)