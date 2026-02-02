package com.example.fefumarket.data.models.api

// Модель запроса для частичного обновления объявления.
// Поля со значением null не отправляются на сервер и не изменяются
data class UpdateAdRequest(
    val title: String? = null,
    val description: String? = null,
    val price: Int? = null
)