package com.icmen.codecase.ui.fragment.orders

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrdersPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _orderList = MutableLiveData<Resource<List<Order>>>()
    val orderList: LiveData<Resource<List<Order>>> get() = _orderList

    fun fetchOrders() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            _orderList.value = Resource.Loading()

            firestore.collection("orders").document(userId).collection("userOrders")
                .orderBy("orderDate")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val orders = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Order::class.java)
                    }
                    _orderList.value = Resource.Success(orders)
                }
                .addOnFailureListener { e ->
                    _orderList.value = Resource.Error(e.message ?: "Failed to fetch orders")
                }
        } else {
            _orderList.value = Resource.Error("User not authenticated")
        }
    }
}
