package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrderItemDTO(
    val bookId: Int,
    val count: Int,
    val price: Double,
    val orderId: Int,
    val book: OrderItemBookDTO
)



