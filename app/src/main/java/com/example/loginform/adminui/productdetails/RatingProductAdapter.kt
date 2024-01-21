package com.example.loginform.adminui.productdetails


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.loginform.Model.UserRating
import com.example.loginform.databinding.ItemRatingBinding

class RatingProductAdapter :
    ListAdapter<UserRating, RatingProductAdapter.ItemViewHolder>(diffcallback) {

    inner class ItemViewHolder(private val binding: ItemRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserRating) {
            binding.apply {
                tvUserEmail.text = item.userEmail
                tvUserName.text = item.userName
                ratingBar.rating = item.rating
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


}

private val diffcallback = object : DiffUtil.ItemCallback<UserRating>() {
    override fun areContentsTheSame(
        oldItem: UserRating, newItem: UserRating
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: UserRating, newItem: UserRating
    ): Boolean {
        return oldItem.userId == newItem.userId
    }
}