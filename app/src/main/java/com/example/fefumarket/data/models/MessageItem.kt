package com.example.fefumarket.data.models

// Модель элемента сообщения в чате, используется для отображения текста или стикеров,
// а также для различия сообщений пользователя и собеседника
data class MessageItem(
    val text: String? = null,
    val isUser: Boolean,
    val isSticker: Boolean = false,
    val stickerRes: Int? = null
)