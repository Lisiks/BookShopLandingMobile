package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.ShopDTO
import retrofit2.http.GET

interface ShopApiService {
    @GET("/shops/")
    suspend fun getShops(): Map<Int, ShopDTO>
}