package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.AuthorDTO
import retrofit2.http.GET

interface AuthorApiService {
    @GET("/authors/")
    suspend fun getAuthors(): Map<Int, AuthorDTO>
}