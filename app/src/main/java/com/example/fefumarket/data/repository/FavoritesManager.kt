package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.Ad

// Менеджер для работы с избранными объявлениями в приложении.
// Поддерживает добавление, удаление, проверку и получение всех избранных.
// Хранение осуществляется в памяти (кэш), данные не сохраняются на сервере.
object FavoritesManager {
    private val favorites = mutableListOf<Ad>()

    fun add(ad: Ad) {
        if (!favorites.contains(ad)) favorites.add(ad)
    }

    fun remove(ad: Ad) {
        favorites.remove(ad)
    }

    fun getAll(): List<Ad> = favorites

    fun isFavorite(ad: Ad): Boolean = favorites.contains(ad)
}