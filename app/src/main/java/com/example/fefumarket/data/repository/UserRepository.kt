package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.api.UserOut
import com.example.fefumarket.data.models.api.UserUpdate
import com.example.fefumarket.network.ApiService

// Репозиторий для работы с пользователями через API
class UserRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    
    private var cachedUser: UserOut? = null

    // Получение информации о текущем пользователе
    suspend fun getMe(): UserOut {
        session.getToken() ?: throw Exception("Not authenticated")
        if (cachedUser == null) {
            cachedUser = api.getMe()
        }
        return cachedUser!!
    }

    // Обновление информации о пользователе
    suspend fun updateMe(userUpdate: UserUpdate): UserOut {
        session.getToken() ?: throw Exception("Not authenticated")
        val updated = api.updateMe(userUpdate)
        cachedUser = updated
        return updated
    }

    // Удаление аккаунта
    suspend fun deleteMe() {
        session.getToken() ?: throw Exception("Not authenticated")
        api.deleteMe()
        cachedUser = null
    }

    // Выход из аккаунта
    suspend fun logout() {
        session.getToken() ?: throw Exception("Not authenticated")
        try {
            api.logout()
        } catch (e: Exception) {
            // Игнорируем ошибки при выходе
        } finally {
            session.clear()
            session.clearToken()
            cachedUser = null
        }
    }

    // Очистка кэша
    fun clearCache() {
        cachedUser = null
    }
}
