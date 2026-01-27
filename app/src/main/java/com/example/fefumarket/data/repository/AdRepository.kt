package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.models.api.AdResponse
import com.example.fefumarket.data.models.api.toAd
import com.example.fefumarket.network.ApiService

class AdRepository(
    private val api: ApiService,
    private val session: SessionManager
) {

    private val adsCache = mutableListOf<Ad>()

    // Получение всех объявлений с сервера и кэширование
    suspend fun getAds(): List<Ad> {
        val adsFromServer: List<AdResponse> = api.getAds()
        val mappedAds = adsFromServer.map { it.toAd(sellerName = "Продавец") }
        adsCache.clear()
        adsCache.addAll(mappedAds)
        return adsCache
    }

    // Получение объявления по ID
    suspend fun getAdById(id: String): Ad? {
        return getAds().find { it.id == id }
    }

    // Добавление нового объявления на сервер и в кэш
    suspend fun addAd(ad: Ad) {
        val token = session.getToken() ?: throw Exception("Not authenticated")
        adsCache.add(ad)
    }

    // Поиск объявления по заголовку (для EditPostActivity)
    fun findByTitle(title: String): Ad? {
        return adsCache.find { it.title == title }
    }

    // Обновление объявления на сервер и в кэше
    suspend fun updateAd(updatedAd: Ad) {
        val index = adsCache.indexOfFirst { it.id == updatedAd.id }
        if (index != -1) {
            val token = session.getToken() ?: throw Exception("Not authenticated")
            adsCache[index] = updatedAd
        }
    }

    // Удаление объявления на сервере и в кэше
    suspend fun removeAd(id: String) {
        val token = session.getToken() ?: throw Exception("Not authenticated")
        adsCache.removeAll { it.id == id }
    }

    // Получение только моих объявлений
    suspend fun getMyAds(userName: String): List<Ad> {
        val token = session.getToken() ?: throw Exception("Not authenticated")
        return adsCache.filter { it.seller == userName }
    }
}