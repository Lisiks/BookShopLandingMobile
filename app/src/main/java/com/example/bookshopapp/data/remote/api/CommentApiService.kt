package com.example.bookshopapp.data.remote.api

import com.example.bookshopapp.data.remote.dto.CommentDTO
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface CommentApiService {
    @GET("/books/{bookId}/comments")
    suspend fun getComments(
        @Path("bookId") bookId: Int
    ): List<CommentDTO>


    @GET("/books/{bookId}/comments/me")
    suspend fun getCommentMe(
        @Path("bookId") bookId: Int
    ): CommentDTO

    @FormUrlEncoded
    @POST("/books/{bookId}/comments")
    suspend fun createComment(
        @Path("bookId") bookId: Int,
        @Field("text") text: String
    ): Map<String, String>


    @DELETE("/books/{bookId}/comments")
    suspend fun deleteComment(
        @Path("bookId") bookId: Int
    ): Map<String, String>
}