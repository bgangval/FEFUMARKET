package com.example.fefumarket.network

import com.example.fefumarket.data.models.Ad
import com.example.fefumarket.data.models.LoginRequest
import com.example.fefumarket.data.models.LoginResponse
import com.example.fefumarket.data.models.api.AdResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface ApiService {

    @POST("auth/login")
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @GET("ads")
    suspend fun getAds(): List<AdResponse>
}
