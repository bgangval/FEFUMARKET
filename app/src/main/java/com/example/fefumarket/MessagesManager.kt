package com.example.fefumarket

object MessagesManager {

    private val chats = mutableMapOf<String, ChatItem>() // chatId -> ChatItem

    fun getAllChats(): List<ChatItem> = chats.values.toList()

    fun getOrCreateChat(chatId: String, sellerName: String, productName: String, avatarResId: Int): ChatItem {
        return chats.getOrPut(chatId) {
            ChatItem(sellerName, productName, "", avatarResId)
        }
    }

    fun addMessage(chatId: String, message: String) {
        chats[chatId]?.let {
            it.lastMessage = message
        }
    }
}