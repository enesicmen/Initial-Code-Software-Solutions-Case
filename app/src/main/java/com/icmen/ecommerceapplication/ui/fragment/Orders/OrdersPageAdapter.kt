package com.icmen.ecommerceapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
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
        private val onClicked: RecyclerItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                viewGroup: ViewGroup,
                onClicked: RecyclerItemClickListener
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val context = viewGroup.context
                val binding = RowOrdersBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding = binding, onClicked = onClicked, mContext = context)
            }
        }

        init {
            itemView.setOnClickListener { onClicked(adapterPosition) }
        }

        @SuppressLint("StringFormatMatches")
        fun bind(item: Order) {
            binding.apply {
                totalAmountTextView.text = item.totalAmount
                orderDate.text = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(item.orderDate)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, onClicked)
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orders[position])
    }
}
