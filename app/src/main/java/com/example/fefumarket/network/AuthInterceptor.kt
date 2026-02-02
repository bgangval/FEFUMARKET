package com.example.fefumarket.network

import android.util.Log
import com.example.fefumarket.data.repository.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

// Interceptor, который добавляет Authorization header с Bearer-токеном
class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = sessionManager.getToken()

        Log.d("AuthInterceptor", "Token from SessionManager: $token")

        val request = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        return chain.proceed(request)
    }
}