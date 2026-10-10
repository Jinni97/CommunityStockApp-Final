package com.example.communitystockapp.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("id") val id: Long? = null,
    @SerialName("product_name") val productName: String,
    @SerialName("stock_qty") val stockQty: Int,
    @SerialName("price") val price: Double
)