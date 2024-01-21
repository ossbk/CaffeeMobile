/*
package com.example.loginform.ModelView

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.loginform.Model.ProductItem
import com.example.loginform.R

class ProductListAdapter(private val productList: List<ProductItem>) :
    RecyclerView.Adapter<ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.coffee_layout, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val productItem = productList[position]
        holder.bind(productItem)
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}

class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(productItem: ProductItem) {
        // Bind data to the views in the item layout
        itemView.findViewById<TextView>(R.id.nameTextView).text = productItem.name
        itemView.findViewById<TextView>(R.id.priceTextView).text = "$${productItem.price}"

       */
/* // Implement click listener for item selection
        itemView.setOnClickListener {
            // Add the selected product item to the cart
            Cart.items.add(productItem)
            Toast.makeText(itemView.context, "${productItem.name} added to cart", Toast.LENGTH_SHORT).show()
        }*//*

    }
}
*/
