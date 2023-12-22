package com.example.loginform.ui.homepage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.loginform.R
import com.example.loginform.databinding.ActivityHomePageBinding
import com.example.loginform.ui.homepage.cart.CartFragment
import com.example.loginform.ui.homepage.mainlayout.CoffeesCakesFragment
import com.example.loginform.ui.homepage.orders.OrdersFragment
import com.example.loginform.ui.homepage.search.SearchFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coffeeFragment = CoffeesCakesFragment()
        val cartFragment = CartFragment()
        val searchFragment = SearchFragment()
        val ordersFragment = OrdersFragment()

        setCurrentFragment(coffeeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(coffeeFragment)
                R.id.cart -> setCurrentFragment(cartFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.orders -> setCurrentFragment(ordersFragment)

            }
            true
        }

    }


    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}