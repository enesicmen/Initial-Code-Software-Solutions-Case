package com.icmen.codecase.ui.fragment.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _orderList = MutableLiveData<List<Order>>()
    val orderList: LiveData<List<Order>> get() = _orderList

    fun fetchOrders() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("orders").document(userId).collection("userOrders")
                .orderBy("orderDate")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val orders = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Order::class.java)
                    }
                    _orderList.value = orders
                }
                .addOnFailureListener { e ->
                    _orderList.value = emptyList()
                }
        } else {
            _orderList.value = emptyList()
        }
    }
}
