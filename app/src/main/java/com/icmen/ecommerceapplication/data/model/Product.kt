package com.icmen.ecommerceapplication.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val body : String = "",
    val currency : String = "",
    val description : String = "",
    val price : Double = 0.0,
    val productColor : String = "",
    val productId: String = "",
    val productImage: String = "",
    val productName: String = "",
    val productType: String = "",
    val listiningDate: String = ""
) : Parcelable