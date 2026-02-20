package com.example.fefumarket.data.models.api

// Модель ответа сервера для списка продуктов/объявлений
data class ProductsResponse(
    val items: List<ProductOut>,
    val total: Int,
    val page: Int,
    val page_size: Int
)