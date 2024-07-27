package com.icmen.ecommerceapplication.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.adapters.ProductsAdapter
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentProductsBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.common.RecyclerItemClickListener
import com.icmen.ecommerceapplication.ui.fragment.Products.ProductsPageViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsPageFragment : BaseFragment<FragmentProductsBinding, ProductsPageViewModel>() {

    private lateinit var mProductsAdapter: ProductsAdapter
    private var mProductList: MutableList<Product> = mutableListOf()
    private lateinit var db: FirebaseFirestore

    override fun setViewModelClass() = ProductsPageViewModel::class.java

    override fun setViewBinding(): FragmentProductsBinding =
        FragmentProductsBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        initProductsAdapter()
        fetchProducts()
    }

    private fun initProductsAdapter() {
        mProductsAdapter = ProductsAdapter(mProductList, object : RecyclerItemClickListener {
            /*
            override fun onItemClick(position: Int) {
                val productId = mProductList[position].id
                val action = ProductsPageFragmentDirections.actionProductsFragmentToProductDetailFragment(productId)
                findNavController().navigate(action)
            }

             */

            override fun invoke(position: Int) {

            }
        })
        getViewBinding()?.rvProducts?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mProductsAdapter
        }
    }

    private fun fetchProducts() {
        getViewBinding()?.progressBar?.visibility = View.VISIBLE
        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                mProductList.clear()
                for (document in result) {
                    val product = document.toObject(Product::class.java)
                    mProductList.add(product)
                }
                mProductsAdapter.notifyDataSetChanged()
                getViewBinding()?.progressBar?.visibility = View.GONE
            }
            .addOnFailureListener {
                getViewBinding()?.progressBar?.visibility = View.GONE
                // Hata durumunda yapılacak işlemler
            }
    }
}
