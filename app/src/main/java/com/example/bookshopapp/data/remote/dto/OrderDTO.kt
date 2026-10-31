package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OrderDTO(
    val id: Int,
    val shopId: Int,
    val status: String,
    val datetime: String,
    val userId: String?,
    val user: UserDTO?,
    val shop: ShopDTO,
    val items: List<OrderItemDTO>
)
