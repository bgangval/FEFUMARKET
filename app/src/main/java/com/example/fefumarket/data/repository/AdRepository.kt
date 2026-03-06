package com.example.fefumarket.data.repository

import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.models.api.*
import com.example.fefumarket.network.ApiService

// Репозиторий для работы с объявлениями: получение с сервера, кэширование,
// добавление, обновление, удаление и фильтрация объявлений
class AdRepository(
    private val api: ApiService,
    private val session: SessionManager
) {

    private val adsCache = mutableListOf<Ad>()
    private var currentUser: UserOut? = null

    // Получение информации о текущем пользователе (кэшируется)
    private suspend fun getCurrentUser(): UserOut {
        if (currentUser == null) {
            currentUser = api.getMe()
        }
        return currentUser!!
    }

    // Получение всех объявлений с сервера, маппинг в внутреннюю модель и кэширование
    suspend fun getAds(): List<Ad> {
        val response = api.getProducts()
        val adsFromServer = response.items
        
        // Получаем информацию о пользователях для маппинга sellerName
        // Пока используем "Продавец" как дефолт, можно улучшить, получая информацию о каждом owner_id
        val mappedAds = adsFromServer.map { productOut ->
            productOut.toAd(sellerName = "Продавец")
        }
        
        adsCache.clear()
        adsCache.addAll(mappedAds)
        return adsCache
    }

    // Получение объявления по ID с сервера
    suspend fun getAdById(id: String): Ad? {
        try {
            val productId = id.toIntOrNull() ?: return null
            val productOut = api.getProduct(productId)
            return productOut.toAd(sellerName = "Продавец")
        } catch (e: Exception) {
            // Если не найдено на сервере, ищем в кэше
            return adsCache.find { it.id == id }
        }
    }

    // Добавление нового объявления на сервер
    suspend fun addAd(ad: Ad): Ad {
        session.getToken() ?: throw Exception("Not authenticated")
        
        val productCreate = ad.toProductCreate()
        val productOut = api.createProduct(productCreate)
        
        val newAd = productOut.toAd(sellerName = session.getUserName() ?: "Продавец")
        
        // Обновляем кэш
        adsCache.add(newAd)
        
        return newAd
    }

    // Поиск объявления по заголовку (сначала в кэше, потом на сервере)
    suspend fun findByTitle(title: String): Ad? {
        // Сначала ищем в кэше
        val cached = adsCache.find { it.title == title }
        if (cached != null) return cached
        
        // Если не найдено, загружаем все объявления и ищем там
        val allAds = getAds()
        return allAds.find { it.title == title }
    }

    // Обновление объявления на сервере
    suspend fun updateAd(updatedAd: Ad): Ad {
        session.getToken() ?: throw Exception("Not authenticated")
        
        val productId = updatedAd.id.toIntOrNull() 
            ?: throw Exception("Invalid product ID: ${updatedAd.id}")
        
        val productUpdate = updatedAd.toProductUpdate()
        val productOut = api.updateProduct(productId, productUpdate)
        
        val updated = productOut.toAd(sellerName = session.getUserName() ?: "Продавец")
        
        // Обновляем кэш
        val index = adsCache.indexOfFirst { it.id == updatedAd.id }
        if (index != -1) {
            adsCache[index] = updated
        } else {
            adsCache.add(updated)
        }
        
        return updated
    }

    // Удаление объявления с сервера
    suspend fun removeAd(id: String) {
        session.getToken() ?: throw Exception("Not authenticated")
        
        val productId = id.toIntOrNull() 
            ?: throw Exception("Invalid product ID: $id")
        
        api.deleteProduct(productId)
        
        // Удаляем из кэша
        adsCache.removeAll { it.id == id }
    }

    // Получение только моих объявлений (фильтруем на клиенте)
    suspend fun getMyAds(): List<Ad> {
        session.getToken() ?: throw Exception("Not authenticated")
        
        // Получаем текущего пользователя
        val user = getCurrentUser()

        // Загружаем объявления с сервера и фильтруем по owner_id текущего пользователя
        val myAds = api.getProducts().items
            .filter { product -> product.owner_id == user.id }
            .map { product -> product.toAd(sellerName = user.name) }

        adsCache.clear()
        adsCache.addAll(myAds)
        return myAds
    }

    // ===== FAVORITES =====
    
    // Добавление объявления в избранное на сервере
    suspend fun addFavorite(productId: Int) {
        session.getToken() ?: throw Exception("Not authenticated")
        api.addFavorite(productId)
    }

    // Удаление объявления из избранного на сервере
    suspend fun removeFavorite(productId: Int) {
        session.getToken() ?: throw Exception("Not authenticated")
        api.removeFavorite(productId)
    }

    // Получение всех избранных объявлений с сервера
    suspend fun getFavorites(): List<Ad> {
        session.getToken() ?: throw Exception("Not authenticated")
        val response = api.getFavorites()
        return response.items.map { productOut ->
            productOut.toAd(sellerName = "Продавец")
        }
    }

    // ===== PRODUCT IMAGES =====
    
    // Загрузка изображения для продукта
    suspend fun uploadProductImage(productId: Int, imagePart: okhttp3.MultipartBody.Part): ProductImageOut {
        session.getToken() ?: throw Exception("Not authenticated")
        return api.addProductImage(productId, imagePart)
    }
}
