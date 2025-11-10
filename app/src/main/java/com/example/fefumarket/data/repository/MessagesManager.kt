package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.ChatItem

object MessagesManager {

    private val chats = mutableMapOf<String, ChatItem>() // chatId -> ChatItem

    fun getAllChats(): List<ChatItem> = chats.values.toList()

    fun getOrCreateChat(
        chatId: String,
        sellerName: String,
        productName: String,
        avatarUri: String
    ): ChatItem {
        return chats.getOrPut(chatId) {
            ChatItem(
                sellerName = sellerName,
                productName = productName,
                lastMessage = "",
                avatarUri = avatarUri // теперь строка
            )
        }
    }

    fun addMessage(chatId: String, message: String) {
        chats[chatId]?.let {
            it.lastMessage = message
        }
    }
}