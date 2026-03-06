package com.example.fefumarket.network

import android.content.Context
import com.example.fefumarket.data.repository.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Retrofit-клиент для работы с backend FEFUMarket
object RetrofitClient {

    const val BASE_URL = "http://10.0.2.2:8000/" // localhost для Android Emulator

    fun resolveUrl(pathOrUrl: String?): String? {
        if (pathOrUrl.isNullOrBlank()) return null
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            return pathOrUrl
        }
        val normalizedPath = if (pathOrUrl.startsWith("/")) pathOrUrl else "/$pathOrUrl"
        return BASE_URL.removeSuffix("/") + normalizedPath
    }

    fun create(context: Context): ApiService {
        val sessionManager = SessionManager(context)

        // Логирование HTTP-запросов
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttp клиент с авторизацией и логированием
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionManager)) // Bearer token
            .addInterceptor(logging)
            .build()

        // Retrofit
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
