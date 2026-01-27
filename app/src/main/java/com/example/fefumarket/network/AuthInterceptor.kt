package com.example.fefumarket.network

import com.example.fefumarket.data.repository.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Log

class AuthInterceptor(
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = sessionManager.getToken()

        Log.d("AUTH_INTERCEPTOR", "Token from SessionManager: $token")

        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }

        return chain.proceed(request)
    }
}
