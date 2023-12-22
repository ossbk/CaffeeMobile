package com.example.loginform.ui.homepage.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loginform.Model.CartProduct
import com.example.loginform.databinding.CartLayoutBinding

class CartAdapter : ListAdapter<CartProduct, CartAdapter.ItemViewHolder>(diffcallback) {
    var minusClicked: ((CartProduct) -> Unit)? = null
    var plusClicked: ((CartProduct) -> Unit)? = null
    var deleteClicked: ((CartProduct) -> Unit)? = null

    inner class ItemViewHolder(private val binding: CartLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartProduct) {
            binding.apply {
                tvTitle.text = item.prodName
                tvDes.text = item.prodDes
                coffeeCount.text = item.qty.toString()
                tvPrice.text = "$${item.prodPrice}"
                tvPriceTotal.text = item.totalPrice()
                Glide.with(binding.root).load(item.prodImgUrl).into(binding.ivProduct)
            }
        }

        init {
            binding.removeCoffee.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                if (item.qty > 1) {
                     minusClicked?.invoke(item)
                } else {
                     deleteClicked?.invoke(item)
                }
            }
            binding.addCoffee.setOnClickListener {
                plusClicked?.invoke(getItem(absoluteAdapterPosition))
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CartLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

private val diffcallback = object : DiffUtil.ItemCallback<CartProduct>() {
    override fun areContentsTheSame(
        oldItem: CartProduct, newItem: CartProduct
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: CartProduct, newItem: CartProduct
    ): Boolean {
        return oldItem.cartId == newItem.cartId
    }
}