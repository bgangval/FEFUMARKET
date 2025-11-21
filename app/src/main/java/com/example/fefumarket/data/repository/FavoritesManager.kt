package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.Ad

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