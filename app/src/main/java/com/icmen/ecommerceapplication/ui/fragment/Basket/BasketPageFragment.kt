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
        // Firestore ve FirebaseAuth başlatma
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        // RecyclerView ayarları
        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems) { position -> onBasketItemClicked(position) }
        getViewBinding()?.rvBasket?.adapter = basketAdapter

        // Kullanıcının sepetini al
        getUserBasket()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getUserBasket() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            firestore.collection("basket")
                .document(userId)
                .collection("products")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    basketItems.clear()
                    for (document in querySnapshot.documents) {
                        val product = document.toObject(Product::class.java)
                        if (product != null) {
                            product.quantity = document.getLong("quantity")?.toInt() ?: 0 // Quantity değerini ayarla
                            basketItems.add(product)
                        }
                    }
                    basketAdapter.notifyDataSetChanged() // RecyclerView'u güncelle
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
        // Burada ürün detay sayfasına yönlendirme veya diğer işlemleri yapabilirsiniz.
    }

    override fun setViewModelClass() = BasketPageViewModel::class.java

    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)
}
