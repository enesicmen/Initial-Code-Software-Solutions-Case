package com.icmen.ecommerceapplication.ui.fragment.ProductDetail

import android.os.Bundle
import com.bumptech.glide.Glide
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentProductDetailBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.fragment.Products.ProductsPageViewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding, ProductsPageViewModel>() {

    private lateinit var product: Product

    override fun setViewModelClass() = ProductsPageViewModel::class.java

    override fun setViewBinding(): FragmentProductDetailBinding =
        FragmentProductDetailBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        arguments?.let {
            product = it.getParcelable("product")!!
        }

        showProductDetails(product)
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
}
