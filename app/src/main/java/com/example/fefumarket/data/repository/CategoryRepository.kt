package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.api.CategoryCreate
import com.example.fefumarket.data.models.api.CategoryOut
import com.example.fefumarket.network.ApiService

// Репозиторий для работы с категориями через API
class CategoryRepository(
    private val api: ApiService,
    private val session: SessionManager
) {
    
    private var cachedCategories: List<CategoryOut>? = null

    // Получение всех категорий
    suspend fun getCategories(): List<CategoryOut> {
        if (cachedCategories == null) {
            cachedCategories = api.getCategories()
        }
        return cachedCategories!!
    }

    // Создание новой категории (только для админов)
    suspend fun createCategory(name: String): CategoryOut {
        session.getToken() ?: throw Exception("Not authenticated")
        val category = api.createCategory(CategoryCreate(name = name))
        cachedCategories = null // Сбрасываем кэш
        return category
    }

    // Очистка кэша
    fun clearCache() {
        cachedCategories = null
    }
}
