package com.icmen.codecase.ui.fragment.basket

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.data.model.User
import com.icmen.codecase.databinding.FragmentBasketBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.dialog.CustomDialogWithOneButtonFragment
import com.icmen.codecase.ui.dialog.CustomDialogWithTwoButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketPageFragment : BaseFragment<FragmentBasketBinding>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var basketAdapter: BasketPageAdapter
    private val basketItems: MutableList<Product> = mutableListOf()
    private var totalAmount = 0.0

    private var userAddress = ""

    override fun setViewBinding(): FragmentBasketBinding =
        FragmentBasketBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        getViewBinding()?.rvBasket?.layoutManager = LinearLayoutManager(requireContext())
        basketAdapter = BasketPageAdapter(basketItems, { }) { position, newQuantity ->
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
        getUserInfo()
    }

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
                    val dialog = CustomDialogWithOneButtonFragment.newInstance("HATA", "${e.message}")

                    dialog.onOkClicked = {}

                    dialog.show(requireActivity().supportFragmentManager, "customDialog")

                    getViewBinding()?.progressBar?.visibility = View.GONE
                }
        } else {
            val dialog = CustomDialogWithOneButtonFragment.newInstance("HATA", "Önce giriş yapmalısınız ")

            dialog.onOkClicked = {}

            dialog.show(requireActivity().supportFragmentManager, "customDialog")
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

        val dialog = CustomDialogWithTwoButtonFragment.newInstance("Ürünü Sil", "Bu ürünü sepetten silmek ister misiniz ? ")

        dialog.onYesClicked = {
            deleteBasketItem(position)
        }
        dialog.onNoClicked = {

        }

        dialog.show(requireActivity().supportFragmentManager, "customDialog")
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


    private fun openPaymentPage() {
        getViewBinding()?.btnBuy?.setOnClickListener {
            if (basketItems.isNotEmpty()) {
                val actionDetail = BasketPageFragmentDirections.actionBasketPageFragmentToPaymentPageFragment(
                    products = basketItems.toTypedArray(),
                    amount = totalAmount.toString(),
                    userAddress = userAddress
                )
                findNavController().navigate(actionDetail)
            } else {
                Toast.makeText(context, "Sepetiniz boş", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserInfo() {
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val userModel = document.toObject(User::class.java)
                        userModel?.let { user ->
                            userAddress = user.address
                        }
                    }
                }
                .addOnFailureListener { e ->
                    val dialog = CustomDialogWithOneButtonFragment.newInstance("HATA", "${e.message}")

                    dialog.onOkClicked = {}

                    dialog.show(requireActivity().supportFragmentManager, "customDialog")
                }
        }
    }

}
