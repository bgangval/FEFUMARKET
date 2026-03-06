package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.api.ChatOut
import com.example.fefumarket.data.models.api.MessageCreate
import com.example.fefumarket.data.models.api.MessageOut
import com.example.fefumarket.network.ApiService

// Репозиторий для работы с чатами через API
class ChatRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    private var currentUserIdCache: Int? = null

    // Получение ID текущего пользователя (кэшируется)
    suspend fun getCurrentUserId(): Int {
        session.getToken() ?: throw Exception("Not authenticated")
        if (currentUserIdCache == null) {
            currentUserIdCache = api.getMe().id
        }
        return currentUserIdCache!!
    }

    // Получение или создание чата для продукта
    suspend fun getOrCreateChat(productId: Int): ChatOut {
        session.getToken() ?: throw Exception("Not authenticated")
        return api.getOrCreateChat(productId)
    }

    // Получение всех чатов пользователя
    suspend fun getMyChats(): List<ChatOut> {
        session.getToken() ?: throw Exception("Not authenticated")
        return api.getMyChats()
    }

    // Получение конкретного чата по ID
    suspend fun getChat(chatId: Int): ChatOut {
        session.getToken() ?: throw Exception("Not authenticated")
        return api.getChat(chatId)
    }

    // Отправка сообщения в чат
    suspend fun sendMessage(chatId: Int, text: String): MessageOut {
        session.getToken() ?: throw Exception("Not authenticated")
        return api.sendMessage(chatId, MessageCreate(text = text))
    }
}
