package com.icmen.ecommerceapplication.ui.fragment.Basket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.adapters.BasketPageAdapter
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentBasketBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment

class BasketPageFragment : BaseFragment<FragmentBasketBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var basketAdapter: BasketPageAdapter
    private val basketItems: MutableList<Product> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems) { position -> onBasketItemClicked(position) }
        getViewBinding()?.rvBasket?.adapter = basketAdapter

        getUserBasket()
    }
    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserBasket() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            getViewBinding()?.progressBar?.visibility = View.VISIBLE

            firestore.collection("basket")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    basketItems.clear()
                    for (document in querySnapshot.documents) {
                        val product = document.toObject(Product::class.java)
                        if (product != null) {
                            product.quantity = document.getLong("quantity")?.toInt() ?: 0
                            basketItems.add(product)
                        }
                    }
                    basketAdapter.notifyDataSetChanged()

                    // Toplam tutarı güncelle
                    updateTotalAmount()

                    getViewBinding()?.progressBar?.visibility = View.GONE
                }
                .addOnFailureListener { e ->
                    showToast("Sepet alınırken hata oluştu: ${e.message}")

                    getViewBinding()?.progressBar?.visibility = View.GONE
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


    @SuppressLint("DefaultLocale")
    private fun updateTotalAmount() {
        var totalAmount = 0.0
        for (product in basketItems) {
            totalAmount += product.price * product.quantity
        }

        getViewBinding()?.tvTotalAmount?.text = String.format("%.2f TL", totalAmount)
    }
}
