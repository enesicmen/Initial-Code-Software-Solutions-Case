package com.icmen.ecommerceapplication.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.R
import com.icmen.ecommerceapplication.adapters.ProductsAdapter
import com.icmen.ecommerceapplication.data.model.Product
import com.icmen.ecommerceapplication.databinding.FragmentProductsBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.common.RecyclerItemClickListener
import com.icmen.ecommerceapplication.ui.fragment.ProductDetail.ProductDetailFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductsPageFragment : BaseFragment<FragmentProductsBinding>() {

    private lateinit var mProductsAdapter: ProductsAdapter
    private var mProductList: MutableList<Product> = mutableListOf()
    private lateinit var db: FirebaseFirestore


    override fun setViewBinding(): FragmentProductsBinding =
        FragmentProductsBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        db = FirebaseFirestore.getInstance()
        initProductsAdapter()
        fetchProducts()
    }

    private fun initProductsAdapter() {
        mProductsAdapter = ProductsAdapter(mProductList, object : RecyclerItemClickListener {
            override fun invoke(position: Int) {
                val product = mProductList[position]
                val productDetailFragment = ProductDetailFragment()
                val bundle = Bundle().apply {
                    putParcelable("product", product)
                }
                productDetailFragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, productDetailFragment)
                    .addToBackStack(null)
                    .commit()


            }
        })
        getViewBinding()?.rvProducts?.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = mProductsAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
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
            }
    }
}
