package com.icmen.codecase.ui.fragment.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsPageViewModel @Inject constructor() : ViewModel() {

    private val _productsLiveData = MutableLiveData<List<Product>>()
    val productsLiveData: LiveData<List<Product>> = _productsLiveData

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _loadingLiveData.value = true
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { document -> document.toObject(Product::class.java) }
                _productsLiveData.value = products
                _loadingLiveData.value = false
            }
            .addOnFailureListener { exception ->
                _loadingLiveData.value = false
                // Handle the error here, e.g., log it or update an error LiveData
            }
    }
}
