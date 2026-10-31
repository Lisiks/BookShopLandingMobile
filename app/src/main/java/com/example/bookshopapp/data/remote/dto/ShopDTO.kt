package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ShopDTO(
    val id: Int,
    val phone: String,
    val town: String,
    val street: String,
    val housing: String
)
