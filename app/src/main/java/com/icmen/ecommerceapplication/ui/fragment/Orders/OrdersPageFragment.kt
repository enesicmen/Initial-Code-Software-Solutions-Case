package com.icmen.ecommerceapplication.ui.fragment.orders

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.icmen.ecommerceapplication.data.model.Order
import com.icmen.ecommerceapplication.databinding.FragmentOrdersBinding
import com.icmen.ecommerceapplication.ui.base.BaseFragment
import com.icmen.ecommerceapplication.ui.common.RecyclerItemClickListener
import android.widget.Toast
import com.icmen.ecommerceapplication.adapters.OrdersPageAdapter

class OrdersPageFragment : BaseFragment<FragmentOrdersBinding>(), RecyclerItemClickListener {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth
    private lateinit var orderAdapter: OrdersPageAdapter
    private val orderList: MutableList<Order> = mutableListOf()

    override fun initView(savedInstanceState: Bundle?) {
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        getViewBinding()?.recyclerViewOrders?.layoutManager = LinearLayoutManager(requireContext())
        orderAdapter = OrdersPageAdapter(orderList, this)
        getViewBinding()?.recyclerViewOrders?.adapter = orderAdapter

        fetchOrders()
    }

    override fun setViewBinding(): FragmentOrdersBinding =
        FragmentOrdersBinding.inflate(layoutInflater)

    private fun fetchOrders() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("orders").document(userId).collection("userOrders")
                .orderBy("orderDate")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    orderList.clear()
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        if (order != null) {
                            orderList.add(order)
                        }
                    }
                    orderAdapter.notifyDataSetChanged()
                }
                .addOnFailureListener { e ->
                    showToast("Siparişler alınırken hata oluştu: ${e.message}")
                }
        } else {
            showToast("Önce giriş yapmalısınız.")
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun invoke(position: Int) {

    }
}
