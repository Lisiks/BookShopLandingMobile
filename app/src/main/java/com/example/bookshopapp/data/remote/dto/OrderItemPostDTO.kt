package com.example.bookshopapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


class OrderItemPostDTO (
    val bookId: Int,
    val count: Int
)