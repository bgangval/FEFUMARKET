package com.example.fefumarket.data.models.api

import com.example.fefumarket.data.models.Ad

// Преобразование AdResponse в модель Ad для использования в приложении
fun AdResponse.toAd(
    sellerName: String,
    dorm: String = ""
): Ad {
    return Ad(
        id = id.toString(),
        title = title ?: "",
        price = price?.toString() ?: "0",
        dorm = dorm,
        seller = sellerName,
        description = description ?: "",
        category = "",
        condition = "",
        imageUris = emptyList(),
        isSold = false
    )
}