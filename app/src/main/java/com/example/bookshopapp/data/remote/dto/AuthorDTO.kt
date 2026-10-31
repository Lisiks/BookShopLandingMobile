package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AuthorDTO(
    val id: Int,
    val f: String,
    val i: String,
    val o: String
)
