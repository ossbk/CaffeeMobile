package com.example.loginform.ui.homepage.orders.orderdetails

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.loginform.Model.OrderItem
import com.example.loginform.Model.mRoundOff
import com.example.loginform.databinding.OrderItemLayoutBinding

class OrderItemAdapter : ListAdapter<OrderItem, OrderItemAdapter.ItemViewHolder>(diffcallback) {

    val ratingHashMap: HashMap<String, Float> = hashMapOf()

    inner class ItemViewHolder(private val binding: OrderItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderItem) {
            binding.apply {
                tvTitle.text = item.prodName
                tvDes.text = item.prodDes
                tvNoOfItems.text = item.qty.toString()
                tvPrice.text = "$${item.prodPrice.mRoundOff()}"
                tvPriceTotal.text = "$${item.calculatePrice().mRoundOff()}"
                Glide.with(binding.root).load(item.prodImgUrl).into(binding.ivProduct)

                     ratingBar.rating=item.rating
                if (item.rating!=0.0f){
                    ratingBar.setIsIndicator(true)
                }

            }
        }

        init {
            binding.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
                if (rating > 0) {
                    ratingHashMap[getItem(absoluteAdapterPosition).prodId] = rating
                } else
                    ratingHashMap.remove(getItem(absoluteAdapterPosition).prodId)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            OrderItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    fun isAllProductsRated():Boolean{
        Log.d("cvrr","ratinghashmp=${ratingHashMap.keys.size},$ratingHashMap")
        Log.d("cvrr","currentlist=${currentList.size},$currentList")
        return ratingHashMap.keys.size==currentList.size
    }


}


private val diffcallback = object : DiffUtil.ItemCallback<OrderItem>() {
    override fun areContentsTheSame(
        oldItem: OrderItem, newItem: OrderItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: OrderItem, newItem: OrderItem
    ): Boolean {
        return oldItem.orderItemId == newItem.orderItemId
    }
}