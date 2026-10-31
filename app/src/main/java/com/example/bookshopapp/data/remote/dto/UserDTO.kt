package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class UserDTO(
    val username: String,
    val id: Int,
    val isBlocked: String,
    val isAdmin: String
)
