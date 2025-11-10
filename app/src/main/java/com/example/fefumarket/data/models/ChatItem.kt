package com.example.fefumarket.data.models

data class ChatItem(
    val sellerName: String,
    val productName: String,
    var lastMessage: String,
    val avatarUri: String,
    var isMuted: Boolean = false
)