package com.example.fefumarket

data class ChatItem(
    val sellerName: String,
    val productName: String,
    var lastMessage: String,
    val avatarUri: String,
    var isMuted: Boolean = false
)