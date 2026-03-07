package com.example.fefumarket.data.models.api

import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.models.MessageItem

// Преобразование ChatOut в ChatItem для использования в UI
fun ChatOut.toChatItem(): ChatItem {
    val lastMsg = messages.lastOrNull()?.text ?: ""
    return ChatItem(
        sellerName = seller_name?.takeIf { it.isNotBlank() } ?: "Продавец",
        productName = product_title?.takeIf { it.isNotBlank() } ?: "Товар",
        lastMessage = lastMsg,
        avatarUri = "",
        isMuted = false,
        apiChatId = id
    )
}

// Преобразование MessageOut в MessageItem для использования в UI
fun MessageOut.toMessageItem(currentUserId: Int): MessageItem {
    return MessageItem(
        text = text,
        isUser = sender_id == currentUserId,
        isSticker = false
    )
}
