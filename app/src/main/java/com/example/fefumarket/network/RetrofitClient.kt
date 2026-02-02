package com.example.fefumarket.network

import android.content.Context
import com.example.fefumarket.data.repository.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit-клиент для работы с backend FEFUMarket
object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8000/" // localhost для Android Emulator

    fun create(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        // Логирование HTTP-запросов
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttp клиент с авторизацией и логированием
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager)) // Bearer token
            .addInterceptor(loggingInterceptor)
            .build()

        // Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}