package com.example.finalspicyfood.mui.orderdetails

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.databinding.ActivityOrderdetailsBinding
import com.example.loginform.ui.homepage.orders.AdminOrdersViewModel
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class OrderdetailsActivity : AppCompatActivity() {


    companion object {
        const val ORDER_KEY = "ORDER_KEY"
    }

    private lateinit var binding: ActivityOrderdetailsBinding

    @Inject
    lateinit var prefRepository: PrefRepository

    private val adminOrdersViewModel: AdminOrdersViewModel by viewModels()

    val animalsArray = arrayOf(
        "ORDER STATUS",
        "ORDER DETAILS",

        )


    private var orderDetailsPagerAdapter: OrderDetailsPagerAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderdetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        intent.extras?.apply {
            val orderId: String = getString(ORDER_KEY, "") ?: ""
            if (orderId.isNotEmpty()) {
                adminOrdersViewModel.setSelectedOrder(orderId)
            }
        }

        lifecycleScope.launch {
            adminOrdersViewModel.clickedOrder.collectLatest { om ->
                if (om != null) {
                    binding.textView3.text = om.orderDate.formatSecondsToDate()
                    binding.textView4.text = om.orderId

                    if (prefRepository.currentUser.stringToCustomer()?.cusIsAdmin == true) {
                        binding.tvUserName.visibility = View.VISIBLE
                        binding.tvUserEmail.visibility = View.VISIBLE

                        binding.tvUserName.text = om.userName
                        binding.tvUserEmail.text = om.userEmail
                    }
                }
            }
        }

        binding.ivBack.setOnClickListener {
            finish()
        }
        setupViewPager()


    }


    private fun setupViewPager() {
        binding.viewpager.apply {
            orderDetailsPagerAdapter = OrderDetailsPagerAdapter(supportFragmentManager, lifecycle)
            adapter = orderDetailsPagerAdapter

        }

        TabLayoutMediator(binding.tablayout, binding.viewpager) { tab, position ->
            tab.text = animalsArray[position]
        }.attach()

    }


}


fun Long.formatSecondsToDate(): String {
    val instant = Instant.ofEpochSecond(this)
    val formatter = DateTimeFormatter.ofPattern("hh:mm a dd MMM,yyyy ")
        .withZone(ZoneId.systemDefault()) // Adjust zone if necessary
    return formatter.format(instant)
}


