package com.example.fefumarket

data class ChatItem(
    val sellerName: String,      // Имя продавца
    val productName: String,     // Название товара
    var lastMessage: String,     // Последнее сообщение
    val avatarResId: Int,         // Фото товара
    var isMuted: Boolean = false  //mute
)