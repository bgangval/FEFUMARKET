package com.example.fefumarket.data.models.api

data class ProductsResponse(
    val items: List<AdResponse>,
    val total: Int,
    val page: Int,
    val page_size: Int
)