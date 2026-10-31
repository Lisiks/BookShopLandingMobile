package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
class CommentDTO(
    @SerialName("user_id")
    val userId: Int,
    val datetime: String,
    val test: String,
    val username: String
)

