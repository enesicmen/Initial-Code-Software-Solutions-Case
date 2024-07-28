package com.icmen.ecommerceapplication.ui.fragment.ProductDetail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentProductDetailBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.fragment.Products.ProductsPageViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding, ProductsPageViewModel>() {

    private lateinit var product: Product
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private var quantity: Int = 1

    override fun setViewModelClass() = ProductsPageViewModel::class.java

    override fun setViewBinding(): FragmentProductDetailBinding =
        FragmentProductDetailBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        arguments?.let {
            product = it.getParcelable("product")!!
        }

        showProductDetails(product)

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
            val basketRef = firestore.collection("basket").document(userId).collection("products").document(product.productId)

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
                        "productName" to product.productName,
                        "price" to product.price,
                        "currency" to product.currency,
                        "quantity" to quantity,
                        "productImage" to product.productImage,
                        "userId" to userId,
                        "description" to product.description,
                        "listiningDate" to product.listiningDate,
                        "productColor" to product.productColor,
                        "productId" to product.productId
                    )
                    basketRef.set(productData)
                        .addOnSuccessListener {
                            showToast("Ürün sepete eklendi: ${product.productName}")
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
