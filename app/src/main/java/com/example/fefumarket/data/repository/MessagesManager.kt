package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.ChatItem

// Менеджер чатов в приложении: хранит список чатов, добавляет сообщения
// и создаёт новый чат при необходимости. Данные хранятся в памяти.
object MessagesManager {

    private val chats = mutableMapOf<String, ChatItem>() // chatId -> ChatItem

    // Возвращает все чаты в виде списка
    fun getAllChats(): List<ChatItem> = chats.values.toList()

    // Получает существующий чат по ID или создаёт новый, если его нет
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
                avatarUri = avatarUri
            )
        }
    }

    // Добавляет/обновляет последнее сообщение в чате
    fun addMessage(chatId: String, message: String) {
        chats[chatId]?.let {
            it.lastMessage = message
        }
    }
}