package com.icmen.ecommerceapplication.ui.fragment.ProductDetail

import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentProductDetailBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.fragment.Products.ProductsPageViewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding, ProductsPageViewModel>() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var product: Product
    private var mUserId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firestore = FirebaseFirestore.getInstance() // Burada başlatma
        auth = FirebaseAuth.getInstance() // FirebaseAuth'ı başlatma
    }

    override fun setViewModelClass() = ProductsPageViewModel::class.java

    override fun setViewBinding(): FragmentProductDetailBinding =
        FragmentProductDetailBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            product = it.getParcelable("product")!!
        }

        showProductDetails(product)
        val userId = auth.currentUser?.uid
        if (userId != null) {
            mUserId = userId
        }
        getViewBinding()?.btnBasket?.setOnClickListener {
            addToBasket(product)
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
    }

    private fun addToBasket(product: Product) {
        val basketItem = hashMapOf(
            "productId" to product.productId,
            "productName" to product.productName,
            "productColor" to product.productColor,
            "listiningDate" to product.listiningDate,
            "description" to product.description,
            "price" to product.price,
            "currency" to product.currency,
            "productImage" to product.productImage,
            "userId" to mUserId
        )

        firestore.collection("basket")
            .add(basketItem)
            .addOnSuccessListener { documentReference ->
                showToast("Ürün sepete eklendi: ${documentReference.id}") // Hata burada mı?
            }
            .addOnFailureListener { e ->
                showToast("Sepete eklenirken hata oluştu: ${e.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
