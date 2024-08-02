package com.icmen.codecase.ui.fragment.products

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.RowProductsBinding
import com.icmen.codecase.ui.common.RecyclerItemClickListener

class ProductsAdapter(
    private val productList: MutableList<Product>,
    private val onClicked: RecyclerItemClickListener
) : RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RowProductsBinding,
        private val onClicked: RecyclerItemClickListener,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                viewGroup: ViewGroup,
                onClicked: RecyclerItemClickListener,
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val context = viewGroup.context
                val binding = RowProductsBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding = binding, onClicked = onClicked, context)
            }
        }

        init {
            itemView.setOnClickListener { onClicked(adapterPosition) }
        }

        @SuppressLint("StringFormatMatches")
        fun bind(item: Product) {
            binding.apply {
                tvProductName.text = mContext.getString(R.string.product_name, item.productName)
                tvProductDescription.text = item.description
                tvProductPrice.text = mContext.getString(R.string.price, item.price, item.currency)
                Glide.with(mContext).load(item.productImage).into(ivProductImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(viewGroup = parent, onClicked = onClicked)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(item = productList[position])

    override fun getItemCount(): Int = productList.size
}
