package com.icmen.codecase.ui.fragment.orders

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Order
import com.icmen.codecase.databinding.FragmentOrdersBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.common.RecyclerItemClickListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersPageFragment : BaseFragment<FragmentOrdersBinding, OrdersPageViewModel>(), RecyclerItemClickListener {

    private val viewModel: OrdersPageViewModel by viewModels()
    private lateinit var orderAdapter: OrdersPageAdapter
    private val orderList: MutableList<Order> = mutableListOf()

    override fun setViewModelClass() = OrdersPageViewModel::class.java

    override fun setViewBinding(): FragmentOrdersBinding =
        FragmentOrdersBinding.inflate(layoutInflater)

    override fun initView(savedInstanceState: Bundle?) {
        orderAdapter = OrdersPageAdapter(orderList, this)
        getViewBinding()?.recyclerViewOrders?.layoutManager = LinearLayoutManager(requireContext())
        getViewBinding()?.recyclerViewOrders?.adapter = orderAdapter

        observeOrders()
        viewModel.fetchOrders()
    }

    private fun observeOrders() {
        viewModel.orderList.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    getViewBinding()?.progressBar?.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    orderList.clear()
                    orderList.addAll(resource.data ?: emptyList())
                    orderAdapter.notifyDataSetChanged()
                }
                is Resource.Error -> {
                    getViewBinding()?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.error ?: "Failed to fetch orders", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun invoke(position: Int) {}
}
