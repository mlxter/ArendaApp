package com.example.arendainstrumenta.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.arendainstrumenta.data.Product
import com.example.arendainstrumenta.databinding.ProductRvItemBinding

class ProductAdapter:
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(){

    inner class ProductViewHolder(private val binding: ProductRvItemBinding):
        ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                product.offerPercentage?.let {
                    val remainingPricePercentage = 1f - it
                    val priceAfterOffer = remainingPricePercentage * product.price
                    tvNewPrice.text = "${String.format("%.2f",priceAfterOffer)} ₽"
                }
                if (product.offerPercentage == null)
                    tvNewPrice.visibility = View.INVISIBLE
                tvPrice.text = "${product.price} ₽"
                tvName.text = product.name
            }
        }
    }

    private val diffCallBack = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this,diffCallBack)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {
        return ProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener{
            onClick?.invoke(product)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onClick: ((Product) -> Unit)? = null
}