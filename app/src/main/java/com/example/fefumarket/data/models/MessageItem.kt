package com.example.fefumarket.data.models

data class MessageItem(
    val text: String? = null,
    val isUser: Boolean,
    val isSticker: Boolean = false,
    val stickerRes: Int? = null
)