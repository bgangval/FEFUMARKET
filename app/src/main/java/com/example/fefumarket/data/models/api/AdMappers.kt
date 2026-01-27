package com.example.fefumarket.data.models.api

import com.example.fefumarket.data.models.Ad

fun AdResponse.toAd(sellerName: String, dorm: String = ""): Ad {
    return Ad(
        id = id.toString(),
        title = title,
        price = price.toString(),
        dorm = dorm,
        seller = sellerName,
        description = description,
        category = "",
        condition = "",
        imageUris = listOf(),
        isSold = false
    )
}