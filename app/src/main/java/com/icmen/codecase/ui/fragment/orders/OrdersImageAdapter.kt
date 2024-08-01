package com.icmen.codecase.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.RowOrdersImageBinding

class OrdersImageAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<OrdersImageAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RowOrdersImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(viewGroup: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val binding = RowOrdersImageBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding)
            }
        }

        fun bind(product: Product) {
            Glide.with(binding.root.context)
                .load(product.productImage)
                .into(binding.imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }
}
