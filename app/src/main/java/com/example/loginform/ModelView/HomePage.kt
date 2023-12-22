package com.example.loginform.ModelView

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.example.loginform.Model.Cake
import com.example.loginform.Model.Item
import com.example.loginform.R

class HomePage : AppCompatActivity() {
   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val coffeeButton: ImageButton = findViewById(R.id.coffeeButton)
        val cakeButton: ImageButton = findViewById(R.id.cakeButton)

        // Sample data (replace with your actual data)
        val coffeeItems: List<CoffeeItem> = getSampleCoffeeItems()
        val cakeItems: List<CakeItem> = getSampleCakeItems()

        coffeeButton.setOnClickListener {
            val intent = Intent(this, CoffeeListActivity::class.java)
            intent.putParcelableArrayListExtra("COFFEE_ITEMS", ArrayList(coffeeItems))
            startActivity(intent)
        }

        cakeButton.setOnClickListener {
            val intent = Intent(this, CakeListActivity::class.java)
            intent.putParcelableArrayListExtra("CAKE_ITEMS", ArrayList(cakeItems))
            startActivity(intent)
        }
    }

    // Replace these with your actual data retrieval methods
    private fun getSampleCoffeeItems(): List<CoffeeItem> {
        // Return a list of coffee items
    }

    private fun getSampleCakeItems(): List<CakeItem> {
        // Return a list of cake items
    }*/
}

