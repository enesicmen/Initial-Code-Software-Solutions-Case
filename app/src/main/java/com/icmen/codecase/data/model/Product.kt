package com.icmen.codecase.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    var currency: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var productColor: String = "",
    var productId: String = "",
    var productImage: String = "",
    var productName: String = "",
    var productType: String = "",
    var listininDate: String = "",
    var quantity: Int = 0
) : Parcelable
