package com.icmen.codecase.ui.fragment.productDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.Product

class ProductDetailViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _basketResponse = MutableLiveData<String>()
    val basketResponse: LiveData<String> get() = _basketResponse

    fun addToBasket(product: Product, quantity: Int) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val basketRef = firestore.collection("basket").document(userId).collection("products").document(product.productId)

            basketRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentQuantity = document.getLong("quantity") ?: 0
                    basketRef.update("quantity", currentQuantity + quantity)
                        .addOnSuccessListener {
                            _basketResponse.value = "Sepete eklendi, mevcut adet: ${currentQuantity + quantity}"
                        }
                        .addOnFailureListener { e ->
                            _basketResponse.value = "Sepete eklenirken hata oluştu: ${e.message}"
                        }
                } else {
                    val productData = hashMapOf(
                        "productName" to product.productName,
                        "price" to product.price,
                        "currency" to product.currency,
                        "quantity" to quantity,
                        "productImage" to product.productImage,
                        "userId" to userId,
                        "description" to product.description,
                        "listiningDate" to product.listiningDate,
                        "productColor" to product.productColor,
                        "productId" to product.productId
                    )
                    basketRef.set(productData)
                        .addOnSuccessListener {
                            _basketResponse.value = "Ürün sepete eklendi: ${product.productName}"
                        }
                        .addOnFailureListener { e ->
                            _basketResponse.value = "Sepete eklenirken hata oluştu: ${e.message}"
                        }
                }
            }.addOnFailureListener { e ->
                _basketResponse.value = "Sepet kontrol edilirken hata oluştu: ${e.message}"
            }
        } else {
            _basketResponse.value = "Önce giriş yapmalısınız."
        }
    }
}
