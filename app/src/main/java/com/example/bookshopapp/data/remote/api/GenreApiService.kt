package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.GenreDTO
import retrofit2.http.GET


interface GenreApiService {
    @GET("/genres/")
    suspend fun getGenres(): Map<Int, GenreDTO>
}