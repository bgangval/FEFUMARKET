package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.Ad

object AdRepository {

    // Приватный список всех объявлений
    private val adsList = mutableListOf(
        // --- Мои объявления ---
        Ad(
            id = "1",
            title = "Ноутбук Lenovo",
            price = "₽45,000",
            dorm = "Корпус 8",
            seller = "Иван",
            description = "Новый, i7, 16GB RAM",
            category = "Техника",
            condition = "Новое",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/laptop_ic_test"
            )
        ),
        Ad(
            id = "2",
            title = "Кроссовки Nike",
            price = "₽9,000",
            dorm = "Корпус 3",
            seller = "Иван",
            description = "Размер 42, оригинал",
            category = "Обувь",
            condition = "Б/у",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/nike_ic_test"
            )
        ),
        Ad(
            id = "3",
            title = "Книга по Android",
            price = "₽1,200",
            dorm = "Корпус 1",
            seller = "Иван",
            description = "Kotlin для начинающих",
            category = "Для учебы",
            condition = "Новое",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/book_ic_test"
            )
        ),

        // --- Примеры других пользователей ---
        Ad(
            id = "4",
            title = "Айфон 12",
            price = "₽70,000",
            dorm = "Корпус 5",
            seller = "Мария",
            description = "В отличном состоянии, 128GB",
            category = "Техника",
            condition = "Б/у",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/iphone_ic_test"
            )
        ),
        Ad(
            id = "5",
            title = "Монитор Samsung",
            price = "₽12,500",
            dorm = "Корпус 4",
            seller = "Анна",
            description = "27 дюймов, 4K",
            category = "Техника",
            condition = "Б/у",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/monitor_ic_test"
            )
        ),
        Ad(
            id = "6",
            title = "Велосипед Giant",
            price = "₽25,000",
            dorm = "Корпус 6",
            seller = "Петр",
            description = "Горный велосипед, 21 скорость",
            category = "Барахло",
            condition = "Б/у",
            imageUris = listOf(
                "android.resource://com.example.fefumarket/drawable/bike_ic_test"
            )
        )
    )

    val ads: List<Ad>
        get() = adsList

    fun getMyAds(userName: String): List<Ad> = adsList.filter { it.seller == userName }

    fun addAd(ad: Ad) {
        adsList.add(ad)
    }

    fun getById(id: String): Ad? {
        return adsList.find { it.id == id }
    }

    fun updateAd(updatedAd: Ad) {
        val index = adsList.indexOfFirst { it.id == updatedAd.id }
        if (index != -1) adsList[index] = updatedAd
    }

    fun findByTitle(title: String): Ad? {
        return adsList.find { it.title.equals(title, ignoreCase = true) }
    }

    fun removeAd(id: String) {
        val index = adsList.indexOfFirst { it.id == id }
        if (index != -1) {
            adsList.removeAt(index)
        }
    }
}