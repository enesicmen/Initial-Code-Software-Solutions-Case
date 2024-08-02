package com.icmen.codecase.ui.fragment.products

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductsPageViewModel @Inject constructor() : ViewModel() {

    private val _productsLiveData = MutableLiveData<Resource<List<Product>>>()
    val productsLiveData: LiveData<Resource<List<Product>>> = _productsLiveData

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        _productsLiveData.value = Resource.Loading()
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                val products = result.map { document -> document.toObject(Product::class.java) }
                _productsLiveData.value = Resource.Success(products)
            }
            .addOnFailureListener { exception ->
                _productsLiveData.value = Resource.Error(exception.message ?: "An error occurred")
            }
    }
}
