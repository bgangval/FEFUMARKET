package com.example.fefumarket.data.models.api

import com.example.fefumarket.data.models.ChatItem
import com.example.fefumarket.data.models.MessageItem

// Преобразование ChatOut в ChatItem для использования в UI
fun ChatOut.toChatItem(sellerName: String = "Продавец", productName: String = "Товар"): ChatItem {
    val lastMsg = messages.lastOrNull()?.text ?: ""
    return ChatItem(
        sellerName = sellerName,
        productName = productName,
        lastMessage = lastMsg,
        avatarUri = "",
        isMuted = false
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
