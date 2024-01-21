package com.example.loginform.ui.homepage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.R
import com.example.loginform.data.PrefRepository
import com.example.loginform.databinding.ActivityHomePageBinding
import com.example.loginform.ui.homepage.cart.CartFragment
import com.example.loginform.ui.homepage.mainlayout.CoffeesCakesFragment
import com.example.loginform.ui.homepage.orders.OrdersFragment
import com.example.loginform.ui.homepage.search.SearchFragment
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomePageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomePageBinding

    @Inject
    lateinit var prefRepository: PrefRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        FirebaseMessaging.getInstance()
            .subscribeToTopic(prefRepository.currentUser.stringToCustomer()!!.cusId)


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