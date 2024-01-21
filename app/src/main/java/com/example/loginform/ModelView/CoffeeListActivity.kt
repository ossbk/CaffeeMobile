/*
package com.example.loginform.ModelView

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginform.Model.ItemType
import com.example.loginform.Model.ProductItem

import com.example.loginform.R

    class CoffeeListActivity : AppCompatActivity() {

        private val productList: List<ProductItem> = listOf(
            ProductItem("Espresso", 2.5, ItemType.COFFEE),
            ProductItem("Latte", 3.0, ItemType.COFFEE),
            // Add more coffee items as needed
            ProductItem("Chocolate Cake", 4.0, ItemType.CAKE),
            ProductItem("Cheesecake", 5.0, ItemType.CAKE)
            // Add more cake items as needed
        )

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.coffee_layout)

            val recyclerView: RecyclerView = findViewById(R.id.itemRecyclerView)
            val adapter = ProductListAdapter(productList)

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }
    }
*/
