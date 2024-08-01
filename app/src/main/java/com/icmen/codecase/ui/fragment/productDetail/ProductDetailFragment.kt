package com.icmen.codecase.ui.fragment.productDetail

import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentProductDetailBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>() {

    private lateinit var mProduct: Product
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var quantity: Int = 1

    override fun setViewBinding(): FragmentProductDetailBinding =
        FragmentProductDetailBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()



        showProductDetails(mProduct)

        getViewBinding()?.btnDecrease?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityText()
            }
        }


        getViewBinding()?.btnIncrease?.setOnClickListener {
            quantity++
            updateQuantityText()
        }


        getViewBinding()?.btnBasket?.setOnClickListener {
            addToBasket()
        }
    }

    override fun readDataFromArguments() {
        super.readDataFromArguments()
        arguments?.let {
            val safeArgs = ProductDetailFragmentArgs.fromBundle(it)
            mProduct = safeArgs.product
        }
    }

    private fun showProductDetails(product: Product) {
        getViewBinding()?.apply {
            tvProductName.text = product.productName
            tvProductColor.text = product.productColor
            tvListiningDate.text = product.listiningDate
            tvDescription.text = product.description
            tvPrice.text = getString(R.string.price, product.price, product.currency)

            Glide.with(this@ProductDetailFragment)
                .load(product.productImage)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(ivImage)
        }

        updateQuantityText()
    }

    private fun updateQuantityText() {
        getViewBinding()?.tvQuantity?.text = quantity.toString()
    }

    private fun addToBasket() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val basketRef = firestore.collection("basket").document(userId).collection("products").document(mProduct.productId)

            basketRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val currentQuantity = document.getLong("quantity") ?: 0
                    basketRef.update("quantity", currentQuantity + quantity)
                        .addOnSuccessListener {
                            showToast("Sepete eklendi, mevcut adet: ${currentQuantity + quantity}")
                        }
                        .addOnFailureListener { e ->
                            showToast("Sepete eklenirken hata oluştu: ${e.message}")
                        }
                } else {
                    val productData = hashMapOf(
                        "productName" to mProduct.productName,
                        "price" to mProduct.price,
                        "currency" to mProduct.currency,
                        "quantity" to quantity,
                        "productImage" to mProduct.productImage,
                        "userId" to userId,
                        "description" to mProduct.description,
                        "listiningDate" to mProduct.listiningDate,
                        "productColor" to mProduct.productColor,
                        "productId" to mProduct.productId
                    )
                    basketRef.set(productData)
                        .addOnSuccessListener {
                            showToast("Ürün sepete eklendi: ${mProduct.productName}")
                        }
                        .addOnFailureListener { e ->
                            showToast("Sepete eklenirken hata oluştu: ${e.message}")
                        }
                }
            }.addOnFailureListener { e ->
                showToast("Sepet kontrol edilirken hata oluştu: ${e.message}")
            }
        } else {
            showToast("Önce giriş yapmalısınız.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
