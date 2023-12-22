package com.example.loginform.ui.homepage.mainlayout

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.loginform.Model.ProductItem
import com.example.loginform.databinding.CoffeeLayoutBinding

class CoffeeCartAdapter : ListAdapter<ProductItem, CoffeeCartAdapter.ItemViewHolder>(diffcallback) {
    var procuctClicked: ((ProductItem) -> Unit)? = null

    inner class ItemViewHolder(private val binding: CoffeeLayoutBinding) :
        ViewHolder(binding.root) {
        fun bind(item: ProductItem) {
            binding.apply {
                nameTextView.text = item.prodName
                descriptionTextView.text = item.prodDes
                descriptionTextView.text = item.prodDes
                priceTextView.text = "$${item.prodPrice}"
                Glide.with(binding.root).load(item.prodImage).into(binding.coffeeImage)
                if (item.prodNoOfPeopleRated > 0) {
                    val rating = item.prodRating / item.prodNoOfPeopleRated
                    if (rating == 0) {
                        ratingBar.visibility = View.GONE
                    } else {
                        ratingBar.rating = rating.toFloat()
                        ratingBar.visibility = View.VISIBLE
                    }
                }
                else
                    ratingBar.visibility=View.GONE

            }
        }

        init {
            binding.clRoot.setOnClickListener {
                procuctClicked?.invoke(getItem(absoluteAdapterPosition))
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            CoffeeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

private val diffcallback = object : DiffUtil.ItemCallback<ProductItem>() {
    override fun areContentsTheSame(
        oldItem: ProductItem, newItem: ProductItem
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: ProductItem, newItem: ProductItem
    ): Boolean {
        return oldItem.prodId == newItem.prodId
    }
}