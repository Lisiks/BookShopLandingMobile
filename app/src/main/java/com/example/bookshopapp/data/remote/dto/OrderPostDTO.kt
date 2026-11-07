package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderPostDTO(
    val shopId: Int,
    val items: List<Map<String, Int>>
)
