package com.example.fefumarket.network

import com.example.fefumarket.data.models.LoginRequest
import com.example.fefumarket.data.models.LoginResponse
import com.example.fefumarket.data.models.api.CategoryCreate
import com.example.fefumarket.data.models.api.CategoryOut
import com.example.fefumarket.data.models.api.ChatOut
import com.example.fefumarket.data.models.api.MessageCreate
import com.example.fefumarket.data.models.api.MessageOut
import com.example.fefumarket.data.models.api.ProductCreate
import com.example.fefumarket.data.models.api.ProductImageOut
import com.example.fefumarket.data.models.api.ProductOut
import com.example.fefumarket.data.models.api.ProductUpdate
import com.example.fefumarket.data.models.api.ProductsResponse
import com.example.fefumarket.data.models.api.RegisterRequest
import com.example.fefumarket.data.models.api.RegisterResponse
import com.example.fefumarket.data.models.api.UserOut
import com.example.fefumarket.data.models.api.UserUpdate
import retrofit2.http.*

interface ApiService {

    // ===== AUTH =====
    
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("auth/logout")
    suspend fun logout(): Unit

    // ===== USERS =====
    
    @GET("users/me")
    suspend fun getMe(): UserOut

    @PUT("users/me")
    suspend fun updateMe(@Body body: UserUpdate): UserOut

    @DELETE("users/me")
    suspend fun deleteMe(): Unit

    // ===== CATEGORIES =====
    
    @GET("categories/")
    suspend fun getCategories(): List<CategoryOut>

    @POST("categories/")
    suspend fun createCategory(@Body body: CategoryCreate): CategoryOut

    // ===== PRODUCTS =====
    
    @POST("products/")
    suspend fun createProduct(@Body body: ProductCreate): ProductOut

    @GET("products/")
    suspend fun getProducts(): ProductsResponse

    @GET("products/{product_id}")
    suspend fun getProduct(@Path("product_id") productId: Int): ProductOut

    @PUT("products/{product_id}")
    suspend fun updateProduct(
        @Path("product_id") productId: Int,
        @Body body: ProductUpdate
    ): ProductOut

    @DELETE("products/{product_id}")
    suspend fun deleteProduct(@Path("product_id") productId: Int): Unit

    // ===== PRODUCT IMAGES =====
    
    @Multipart
    @POST("products/{product_id}/images")
    suspend fun addProductImage(
        @Path("product_id") productId: Int,
        @Part image: okhttp3.MultipartBody.Part
    ): ProductImageOut

    // ===== FAVORITES =====
    
    @POST("favorites/{product_id}")
    suspend fun addFavorite(@Path("product_id") productId: Int): Unit

    @DELETE("favorites/{product_id}")
    suspend fun removeFavorite(@Path("product_id") productId: Int): Unit

    @GET("favorites/")
    suspend fun getFavorites(): ProductsResponse

    // ===== CHATS =====
    
    @POST("chats/{product_id}")
    suspend fun getOrCreateChat(@Path("product_id") productId: Int): ChatOut

    @GET("chats/")
    suspend fun getMyChats(): List<ChatOut>

    @GET("chats/{chat_id}")
    suspend fun getChat(@Path("chat_id") chatId: Int): ChatOut

    @POST("chats/{chat_id}/messages")
    suspend fun sendMessage(
        @Path("chat_id") chatId: Int,
        @Body body: MessageCreate
    ): MessageOut
}