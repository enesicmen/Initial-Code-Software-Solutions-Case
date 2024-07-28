package com.icmen.ecommerceapplication.ui.fragment.Basket

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.adapters.BasketPageAdapter
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentBasketBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment

class BasketPageFragment : BaseFragment<FragmentBasketBinding, BasketPageViewModel>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var basketAdapter: BasketPageAdapter
    private val basketItems: MutableList<Product> = mutableListOf() // Sepet ürünlerini tutacak liste

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems) { position -> onBasketItemClicked(position) } // Tıklama olayını ekleyin
        getViewBinding()?.rvBasket?.adapter = basketAdapter
        getUserBasket()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserBasket() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("basket")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    basketItems.clear()
                    for (document in querySnapshot.documents) {
                        val product = document.toObject(Product::class.java)
                        if (product != null) {
                            basketItems.add(product)
                        }
                    }
                    basketAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    showToast("Sepet alınırken hata oluştu: ${e.message}")
                }
        } else {
            showToast("Önce giriş yapmalısınız.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun onBasketItemClicked(position: Int) {
        val selectedProduct = basketItems[position]
        showToast("Ürün tıklandı: ${selectedProduct.productName}")
    }
    override fun setViewModelClass() = BasketPageViewModel::class.java

    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)
}
