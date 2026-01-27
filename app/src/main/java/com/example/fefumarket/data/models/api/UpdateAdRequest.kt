package com.example.fefumarket.data.models.api

data class UpdateAdRequest(
    val title: String? = null,
    val description: String? = null,
    val price: Int? = null
)