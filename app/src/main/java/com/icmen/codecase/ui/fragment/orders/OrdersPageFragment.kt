package com.icmen.codecase.ui.fragment.orders

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.icmen.codecase.R
import com.icmen.codecase.data.Resource
import com.icmen.codecase.data.model.Order
import com.icmen.codecase.databinding.FragmentOrdersBinding
import com.icmen.codecase.ui.base.BaseFragment
import com.icmen.codecase.ui.common.RecyclerItemClickListener
import com.icmen.codecase.ui.fragment.custom.CustomDialogWithOneButtonFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrdersPageFragment : BaseFragment<FragmentOrdersBinding, OrdersPageViewModel>(), RecyclerItemClickListener {

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
        getViewModel()?.fetchOrders()
    }

    private fun observeOrders() {
        getViewModel()?.orderList?.observe(viewLifecycleOwner) { resource ->
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
                    val title = getString(R.string.error)
                    val message = getString(R.string.failed_to_fetch_orders)
                    setOneButtonDialog(title,message)
                }
            }
        }
    }
    private fun setOneButtonDialog(title: String, message: String) {
        val dialog = CustomDialogWithOneButtonFragment.newInstance(title, message)
        dialog.onOkClicked = {}
        dialog.show(requireActivity().supportFragmentManager, "customDialog")
    }
    override fun invoke(position: Int) {}
}
