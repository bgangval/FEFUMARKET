package com.example.fefumarket.network

import com.example.fefumarket.data.models.LoginRequest
import com.example.fefumarket.data.models.LoginResponse
import com.example.fefumarket.data.models.api.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Регистрация
    @POST("auth/register/")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    // Логин
    @POST("auth/login/")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    // Список объявлений
    @GET("products/")
    suspend fun getAds(): ProductsResponse
}