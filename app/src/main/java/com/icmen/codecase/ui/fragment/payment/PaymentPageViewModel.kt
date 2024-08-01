package com.icmen.codecase.ui.fragment.payment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PaymentPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _orderSaveStatus = MutableLiveData<Boolean>()
    val orderSaveStatus: LiveData<Boolean> get() = _orderSaveStatus

    fun saveOrderToFirebase(paymentId: String, products: Array<Product>, totalAmount: String, userAddress: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val ordersRef = firestore.collection("orders").document(userId).collection("userOrders")
            val newOrderRef = ordersRef.document()

            val orderData = hashMapOf(
                "products" to products.toList(),
                "totalAmount" to totalAmount,
                "paymentId" to paymentId,
                "orderDate" to System.currentTimeMillis(),
                "address" to userAddress
            )

            newOrderRef.set(orderData)
                .addOnSuccessListener {
                    _orderSaveStatus.value = true
                }
                .addOnFailureListener { e ->
                    _orderSaveStatus.value = false
                }
        } else {
            _orderSaveStatus.value = false
        }
    }

    fun clearBasket() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val basketRef = firestore.collection("basket").document(userId).collection("products")

            basketRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        document.reference.delete()
                    }
                }
            }
        }
    }
}
