package com.example.fefumarket.data.models

// Модель элемента чата, используемая для отображения списка диалогов,
// хранения состояния (последнее сообщение, режим уведомлений) и передачи данных между слоями
data class ChatItem(
    val sellerName: String,
    val productName: String,
    var lastMessage: String,
    val avatarUri: String,
    var isMuted: Boolean = false
)