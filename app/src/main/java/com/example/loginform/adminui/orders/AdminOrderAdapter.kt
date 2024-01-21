package com.example.loginform.ui.homepage.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.finalspicyfood.mui.orderdetails.formatSecondsToDate
import com.example.loginform.Model.Order
import com.example.loginform.Model.OrderStatus
import com.example.loginform.Model.mRoundOff
import com.example.loginform.R
import com.example.loginform.databinding.OrderAdminLayoutBinding

class AdminOrdersAdapter : ListAdapter<Order, AdminOrdersAdapter.ItemViewHolder>(diffcallback) {
    var orderClicked: ((Order) -> Unit)? = null

    inner class ItemViewHolder(private val binding: OrderAdminLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Order) {
            binding.apply {
                totaltv.setText("$${item.orderTotal.mRoundOff()}")
                noofitems.text = item.orderItems.size.toString()
                deliveryStatus.text = item.orderStatus.status
                tvUserName.text = item.userName
                datetv2.text = item.orderDate.formatSecondsToDate()
                tvUserEmail.text = item.userEmail
                when (item.orderStatus) {
                    OrderStatus.ORDERED -> {
                        binding.deliveryStatus.backgroundTintList =
                            ContextCompat.getColorStateList(binding.root.context, R.color.placed)
                    }


                    OrderStatus.PREPARING -> {
                        binding.deliveryStatus.backgroundTintList =
                            ContextCompat.getColorStateList(binding.root.context, R.color.confirmed)
                    }



                    OrderStatus.COMPLETED -> {
                        binding.deliveryStatus.backgroundTintList =
                            ContextCompat.getColorStateList(binding.root.context, R.color.delivered)
                    }

                    else -> {}
                }
            }
        }

        init {
            binding.root.setOnClickListener {
                val item = getItem(absoluteAdapterPosition)
                orderClicked?.invoke(item)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            OrderAdminLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}


private val diffcallback = object : DiffUtil.ItemCallback<Order>() {
    override fun areContentsTheSame(
        oldItem: Order, newItem: Order
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: Order, newItem: Order
    ): Boolean {
        return oldItem.orderId == newItem.orderId
    }
}