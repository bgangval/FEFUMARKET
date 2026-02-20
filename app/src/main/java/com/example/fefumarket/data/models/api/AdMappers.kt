package com.example.fefumarket.data.models.api

import com.example.fefumarket.data.models.Ad

// Преобразование ProductOut в модель Ad для использования в приложении
fun ProductOut.toAd(
    sellerName: String = "Продавец"
): Ad {
    return Ad(
        id = id.toString(),
        title = title,
        price = "₽${price.toInt()}",
        dorm = building,
        seller = sellerName,
        description = description ?: "",
        category = category,
        condition = condition,
        imageUris = emptyList(), // Изображения загружаются отдельно через /products/{id}/images
        isSold = false
    )
}

// Преобразование Ad в ProductCreate для создания нового объявления
fun Ad.toProductCreate(): ProductCreate {
    val priceValue = price.replace(Regex("[^\\d]"), "").toDoubleOrNull() ?: 0.0
    return ProductCreate(
        title = title,
        price = priceValue,
        category = category,
        condition = condition,
        building = dorm,
        description = description.takeIf { it.isNotBlank() }
    )
}

// Преобразование Ad в ProductUpdate для обновления объявления
fun Ad.toProductUpdate(): ProductUpdate {
    val priceValue = price.replace(Regex("[^\\d]"), "").toDoubleOrNull()
    return ProductUpdate(
        title = title,
        price = priceValue,
        category = category.takeIf { it.isNotBlank() },
        condition = condition.takeIf { it.isNotBlank() },
        building = dorm.takeIf { it.isNotBlank() },
        description = description.takeIf { it.isNotBlank() }
    )
}

// Обратная совместимость: AdResponse -> Ad (если где-то еще используется старый формат)
@Deprecated("Используйте ProductOut.toAd()", ReplaceWith("ProductOut.toAd()"))
fun AdResponse.toAd(
    sellerName: String,
    dorm: String = ""
): Ad {
    return Ad(
        id = id.toString(),
        title = title,
        price = price.toString(),
        dorm = dorm.ifEmpty { building },
        seller = sellerName,
        description = description ?: "",
        category = category,
        condition = condition,
        imageUris = emptyList(),
        isSold = false
    )
}