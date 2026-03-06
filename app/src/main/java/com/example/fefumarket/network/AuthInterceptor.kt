package com.example.fefumarket.network

import android.util.Log
import com.example.fefumarket.data.repository.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor, который добавляет Authorization header с Bearer-токеном
class  AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()
        val request = if (!token.isNullOrBlank()) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}