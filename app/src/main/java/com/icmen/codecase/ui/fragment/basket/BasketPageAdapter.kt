package com.icmen.codecase.ui.fragment.basket

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.icmen.codecase.R
import com.icmen.codecase.data.model.Product
import com.icmen.codecase.databinding.RowBasketBinding
import com.icmen.codecase.ui.common.RecyclerItemClickListener

class BasketPageAdapter(
    private val basketItems: MutableList<Product>,
    private val onClicked: RecyclerItemClickListener,
    private val onQuantityChanged: (Int, Int) -> Unit
) : RecyclerView.Adapter<BasketPageAdapter.ViewHolder>() {

    class ViewHolder(
        private val binding: RowBasketBinding,
        private val onQuantityChanged: (Int, Int) -> Unit,
        private val mContext: Context
    ) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun from(
                viewGroup: ViewGroup,
                onQuantityChanged: (Int, Int) -> Unit
            ): ViewHolder {
                val layoutInflater = LayoutInflater.from(viewGroup.context)
                val context = viewGroup.context
                val binding = RowBasketBinding.inflate(layoutInflater, viewGroup, false)
                return ViewHolder(binding = binding, onQuantityChanged = onQuantityChanged, context)
            }
        }

        init {
            binding.btnIncrease.setOnClickListener {
                val currentQuantity = binding.tvQuantityFor.text.toString().toInt()
                val newQuantity = currentQuantity + 1
                binding.tvQuantityFor.text = newQuantity.toString()
                onQuantityChanged(adapterPosition, newQuantity)
            }
            binding.btnDecrease.setOnClickListener {
                val currentQuantity = binding.tvQuantityFor.text.toString().toInt()
                if (currentQuantity > 1) {
                    val newQuantity = currentQuantity - 1
                    binding.tvQuantityFor.text = newQuantity.toString()
                    onQuantityChanged(adapterPosition, newQuantity)
                } else if (currentQuantity == 1) {
                    onQuantityChanged(adapterPosition, 0)
                }
            }
        }

        @SuppressLint("StringFormatMatches")
        fun bind(item: Product) {
            binding.apply {
                tvProductName.text = item.productName
                tvDescription.text = item.description
                tvPrice.text = mContext.getString(R.string.price, item.price, item.currency)
                tvQuantityFor.text = item.quantity.toString()
                Glide.with(mContext).load(item.productImage).into(ivImage)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder.from(viewGroup = parent, onQuantityChanged = onQuantityChanged)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(item = basketItems[position])

    override fun getItemCount(): Int = basketItems.size
}
