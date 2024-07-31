package com.icmen.ecommerceapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icmen.ecommerceapplication.data.model.Order
import com.icmen.ecommerceapplication.databinding.RowOrdersBinding
import com.icmen.ecommerceapplication.ui.common.RecyclerItemClickListener

class OrdersPageAdapter(
    private val orders: List<Order>,
    private val onClicked: RecyclerItemClickListener
) : RecyclerView.Adapter<OrdersPageAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RowOrdersBinding,
        private val onClicked: RecyclerItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(viewGroup: ViewGroup, onClicked: RecyclerItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val binding = RowOrdersBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding, onClicked)
            }
        }

        init {
            itemView.setOnClickListener { onClicked(adapterPosition) }
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(order: Order) {
            binding.apply {
                tvTotalAmount.text = order.totalAmount.toString()
                tvAddress.text = order.address
                tvOrderDate.text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(order.orderDate)

                // Setup horizontal RecyclerView for product images
                rvImage.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
                rvImage.adapter = OrdersImageAdapter(order.products)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onClicked)
    }

    override fun getItemCount(): Int = orders.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position])
    }
}
