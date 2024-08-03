package com.icmen.codecase.ui.fragment.orders

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Order
import com.icmen.codecase.databinding.RowOrdersBinding
import com.icmen.codecase.ui.common.RecyclerItemClickListener

class OrdersPageAdapter(
    private val orders: List<Order>,
    private val onClicked: RecyclerItemClickListener
) : RecyclerView.Adapter<OrdersPageAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RowOrdersBinding,
        private val onClicked: RecyclerItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(viewGroup: ViewGroup, onClicked: RecyclerItemClickListener): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val context = viewGroup.context
                val binding = RowOrdersBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding, onClicked,context)
            }
        }

        init {
            itemView.setOnClickListener { onClicked(adapterPosition) }
        }

        @SuppressLint("SimpleDateFormat", "StringFormatMatches")
        fun bind(order: Order) {
            binding.apply {
                tvTotalAmount.text = mContext.getString(R.string.order_price, order.totalAmount,"TL")
                tvAddress.text = mContext.getString(R.string.order_address, order.address)
                val orderDate = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(order.orderDate)
                tvOrderDate.text = mContext.getString(R.string.order_date, orderDate)
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
