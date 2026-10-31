package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.BookDTO
import com.example.bookshopapp.data.remote.dto.GetBookListDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface BookApiService {
    @GET("/books/")
    suspend fun getBooks(
        @Query("page") page: Int,
        @Query("title") title: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("authorId") authorId: Int? = null,
        @Query("genreId") genreId: Int? = null,
    ): Map<Int, BookDTO>


    @GET("/books/{bookId}")
    suspend fun  getBookById(
        @Path("bookId") bookId: Int
    ): BookDTO


    @Headers("Content-type: application/json")
    @POST("/books/list")
    suspend fun getBooksByList(
        @Body booksList: GetBookListDTO
    ): Map<Int, BookDTO>

}