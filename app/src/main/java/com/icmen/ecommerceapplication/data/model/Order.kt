package com.icmen.ecommerceapplication.data.model

data class Order(
    val products: List<Product> = listOf(),
    val totalAmount: String = "",
    val orderDate: Long = 0L,
    val address : String = ""
)