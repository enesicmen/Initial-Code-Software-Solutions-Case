package com.icmen.codecase.ui.fragment.products

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.FragmentProductsBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.common.RecyclerItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsPageFragment : BaseFragment<FragmentProductsBinding, ProductsPageViewModel>() {

    private lateinit var mProductsAdapter: ProductsAdapter
    private var mProductList: MutableList<Product> = mutableListOf()

    override fun setViewBinding(): FragmentProductsBinding =
        FragmentProductsBinding.inflate(layoutInflater)

    override fun setViewModelClass() = ProductsPageViewModel::class.java
    private val productsPageViewModel: ProductsPageViewModel by viewModels()

    override fun initView(savedInstanceState: Bundle?) {
        initProductsAdapter()
        observeViewModel()

        // Geri tuşuna basıldığında uygulamadan çıkışı sağlama
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity() // Uygulamayı kapat
            }
        })
    }

    private fun initProductsAdapter() {
        mProductsAdapter = ProductsAdapter(mProductList, object : RecyclerItemClickListener {
            override fun invoke(position: Int) {
                val product = mProductList[position]
                val actionDetail = ProductsPageFragmentDirections.actionProductsPageFragmentToProductDetailPageFragment2(product = product)
                findNavController().navigate(actionDetail)
            }
        })
        getViewBinding()?.rvProducts?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mProductsAdapter
        }
    }

    private fun observeViewModel() {
        productsPageViewModel.productsLiveData.observe(viewLifecycleOwner) { products ->
            mProductList.clear()
            mProductList.addAll(products)
            mProductsAdapter.notifyDataSetChanged()
        }

        productsPageViewModel.loadingLiveData.observe(viewLifecycleOwner) { isLoading ->
            getViewBinding()?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}
