package com.icmen.ecommerceapplication.ui.fragment.Basket

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentBasketBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketPageFragment : BaseFragment<FragmentBasketBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var basketAdapter: BasketPageAdapter
    private val basketItems: MutableList<Product> = mutableListOf()
    private var totalAmount = 0.0

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems, { position -> onBasketItemClicked(position) }) { position, newQuantity ->
            if (newQuantity == 0) {
                showDeleteConfirmationDialog(position)
            } else {
                updateBasketItemQuantity(position, newQuantity)
                updateTotalAmount()
            }
        }
        getViewBinding()?.rvBasket?.adapter = basketAdapter

        getUserBasket()
        openPaymentPage()
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

    private fun updateBasketItemQuantity(position: Int, newQuantity: Int) {
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
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Ürün Sil")
            .setMessage("Bu ürünü sepetten silmek ister misiniz?")
            .setPositiveButton("Evet") { dialog, _ ->
                deleteBasketItem(position)
                dialog.dismiss()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun deleteBasketItem(position: Int) {
        val product = basketItems[position]
        basketItems.removeAt(position)
        basketAdapter.notifyItemRemoved(position)
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("basket")
                .document(it)
                .collection("products")
                .document(product.productId)
                .delete()
        }
        updateTotalAmount()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("DefaultLocale")
    private fun updateTotalAmount() {
        totalAmount = 0.0
        for (product in basketItems) {
            totalAmount += product.price * product.quantity
        }
        getViewBinding()?.tvTotalAmount?.text = String.format("%.2f TL", totalAmount)
    }

    private fun onBasketItemClicked(position: Int) {
        val selectedProduct = basketItems[position]
        showToast("Ürün tıklandı: ${selectedProduct.productName}")
    }

    private fun openPaymentPage() {
        getViewBinding()?.btnBuy?.setOnClickListener {
            if (basketItems.isNotEmpty()) {
                val actionDetail = BasketPageFragmentDirections.actionBasketPageFragmentToPaymentPageFragment(
                    products = basketItems.toTypedArray(),
                    amount = totalAmount.toString()
                )
                findNavController().navigate(actionDetail)
            } else {
                Toast.makeText(context, "Sepetiniz boş", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
