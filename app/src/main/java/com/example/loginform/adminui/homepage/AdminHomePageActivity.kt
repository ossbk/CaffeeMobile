package com.example.loginform.ui.homepage

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.loginform.R
import com.example.loginform.databinding.ActivityAdminHomePageBinding
import com.example.loginform.ui.homepage.mainlayout.CoffeeCartViewModel
import com.example.loginform.ui.homepage.mainlayout.CoffeesCakesFragment
import com.example.loginform.ui.homepage.orders.AdminOrdersFragment
import com.example.loginform.ui.homepage.search.SearchFragment
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminHomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminHomePageBinding
    val coffeeCartViewModel: CoffeeCartViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseMessaging.getInstance().subscribeToTopic("admin")

        val coffeeFragment = CoffeesCakesFragment()
        val searchFragment = SearchFragment()
        val ordersFragment = AdminOrdersFragment()

        setCurrentFragment(coffeeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> setCurrentFragment(coffeeFragment)
                R.id.search -> setCurrentFragment(searchFragment)
                R.id.orders -> setCurrentFragment(ordersFragment)
            }
            true
        }

    }

    override fun onResume() {
        super.onResume()
        coffeeCartViewModel.reloadProducts()
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }
}