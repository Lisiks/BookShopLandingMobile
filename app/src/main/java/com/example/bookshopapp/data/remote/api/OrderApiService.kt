package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.OrderDTO
import com.example.bookshopapp.data.remote.dto.OrderPostDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface OrderApiService {
    @GET("/accounts/me/orders")
    suspend fun getOrders(
        @Query("page") page: Int
    ): Map<Int, OrderDTO>


    @Headers("Content-type: application/json")
    @POST("/orders/")
    suspend fun createOrder(
        @Body orderData: OrderPostDTO
    ): Map<String, String>
}