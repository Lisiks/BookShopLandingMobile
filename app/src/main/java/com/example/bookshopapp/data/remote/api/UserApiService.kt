package com.example.bookshopapp.data.remote.api

import androidx.annotation.StyleRes
import com.example.bookshopapp.data.remote.dto.UserDTO
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


interface UserApiService {
    @GET("/accounts/me")
    suspend fun getUserInfo(): UserDTO

    @GET("/accounts/logout")
    suspend fun logout(): Map<String, String>

    @FormUrlEncoded
    @POST("/accounts/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("plainPassword") plainPassword: String
    ): Map<String, String>


    @FormUrlEncoded
    @POST("/accounts/")
    suspend fun register(
        @Field("username") username: String,
        @Field("plainPassword") plainPassword: String
    ): Map<String, String>

}