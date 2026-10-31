package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemBookDTO(
    val id: Int,
    val title: String
)
