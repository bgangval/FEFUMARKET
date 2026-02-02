package com.example.fefumarket.network

import com.example.fefumarket.data.models.LoginRequest
import com.example.fefumarket.data.models.LoginResponse
import com.example.fefumarket.data.models.api.RegisterRequest
import com.example.fefumarket.data.models.api.RegisterResponse
import com.example.fefumarket.data.models.api.AdResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Ответ списка объявлений (как приходит с бэка)
data class ProductsResponse(
    val items: List<AdResponse>,
    val total: Int,
    val page: Int,
    val page_size: Int
)

// Retrofit API
interface ApiService {

    // Регистрация
    @POST("auth/register")
    suspend fun register(
        @Body body: RegisterRequest
    ): RegisterResponse

    // Авторизация
    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    // Получение объявлений
    @GET("products")
    suspend fun getAds(): ProductsResponse
}