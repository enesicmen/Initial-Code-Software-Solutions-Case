package com.icmen.codecase.ui.fragment.basket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.data.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BasketPageViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _basketItemsLiveData = MutableLiveData<Resource<List<Product>>>()
    val basketItemsLiveData: LiveData<Resource<List<Product>>> = _basketItemsLiveData

    private val _totalAmountLiveData = MutableLiveData<Double>()
    val totalAmountLiveData: LiveData<Double> = _totalAmountLiveData

    private val _userAddressLiveData = MutableLiveData<String>()
    val userAddressLiveData: LiveData<String> = _userAddressLiveData

    private val _progressVisibility = MutableLiveData<Boolean>()
    val progressVisibility: LiveData<Boolean> = _progressVisibility

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    init {
        getUserBasket()
        getUserInfo()
    }

    private fun getUserBasket() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            _basketItemsLiveData.value = Resource.Loading()
            _progressVisibility.value = true

            firestore.collection("basket")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val basketItems = querySnapshot.documents.mapNotNull { document ->
                        document.toObject(Product::class.java)?.apply {
                            quantity = document.getLong("quantity")?.toInt() ?: 0
                        }
                    }
                    _basketItemsLiveData.value = Resource.Success(basketItems)
                    updateTotalAmount(basketItems)
                    _progressVisibility.value = false
                }
                .addOnFailureListener { e ->
                    _basketItemsLiveData.value = Resource.Error(e.message ?: "An error occurred")
                    _progressVisibility.value = false
                }
        } else {
            _errorLiveData.value = "Önce giriş yapmalısınız"
        }
    }

    private fun updateTotalAmount(basketItems: List<Product>) {
        val totalAmount = basketItems.sumOf { it.price * it.quantity }
        _totalAmountLiveData.value = totalAmount
    }

    fun updateBasketItemQuantity(position: Int, newQuantity: Int) {
        val basketItems = (_basketItemsLiveData.value as? Resource.Success)?.data?.toMutableList() ?: return
        val product = basketItems[position]
        product.quantity = newQuantity

        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("basket")
                .document(it)
                .collection("products")
                .document(product.productId)
                .update("quantity", newQuantity)
        }

        basketItems[position] = product
        _basketItemsLiveData.value = Resource.Success(basketItems)
        updateTotalAmount(basketItems)
    }

    fun deleteBasketItem(position: Int) {
        val basketItems = (_basketItemsLiveData.value as? Resource.Success)?.data?.toMutableList() ?: return
        val product = basketItems.removeAt(position)

        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("basket")
                .document(it)
                .collection("products")
                .document(product.productId)
                .delete()
        }

        _basketItemsLiveData.value = Resource.Success(basketItems)
        updateTotalAmount(basketItems)
    }

    private fun getUserInfo() {
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it)
                .get()
                .addOnSuccessListener { document ->
                    val userModel = document.toObject(User::class.java)
                    userModel?.let { user ->
                        _userAddressLiveData.value = user.address
                    }
                }
                .addOnFailureListener { e ->
                    _errorLiveData.value = e.message
                }
        }
    }
}
