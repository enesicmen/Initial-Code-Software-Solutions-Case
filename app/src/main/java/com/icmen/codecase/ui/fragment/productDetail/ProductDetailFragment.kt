package com.icmen.codecase.ui.fragment.productDetail

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentProductDetailBinding
import com.icmen.codecase.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment

@AndroidEntryPoint
class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding, ProductDetailViewModel>() {

    private lateinit var mProduct: Product
    private var quantity: Int = 1

    override fun setViewBinding(): FragmentProductDetailBinding =
        FragmentProductDetailBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        showProductDetails(mProduct)
        setButtonDecrease()
        setButtonIncrease()
        setButtonBasket()
        observeBasketResponse()
    }
    private fun setButtonDecrease(){
        getViewBinding()?.btnDecrease?.setOnClickListener {
            if (quantity > 1) {
                quantity--
                updateQuantityText()
            }
        }
    }
    private fun setButtonIncrease(){
        getViewBinding()?.btnIncrease?.setOnClickListener {
            quantity++
            updateQuantityText()
        }
    }
    private fun setButtonBasket(){
        getViewBinding()?.btnBasket?.setOnClickListener {
            getViewModel()?.addToBasket(mProduct, quantity)
        }
    }

    override fun setViewModelClass() = ProductDetailViewModel::class.java

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
            tvListiningDate.text = product.listininDate
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

    private fun observeBasketResponse() {
        getViewModel()?.basketResponse?.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    val title = getString(R.string.success)
                    val message = getString(R.string.add_to_basket_success)
                    setOneButtonDialog(title, message)
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                }
            }
        }
    }
    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
}
