package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BookDTO(
    val id: Int,
    val title: String,
    val description: String?,
    val authorId: Int,
    val genreId: Int,
    val pageCount: Int,
    val writeYear: Int,
    val isbn: String,
    val price: Double,
    val imageFilePath: String,
    val demoFilePath: String,
    val author: AuthorDTO,
    val genre: GenreDTO
)

