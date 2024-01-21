package com.example.loginform.ui.homepage.orders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.finalspicyfood.mui.orderdetails.OrderdetailsActivity
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentOrdersBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdminOrdersFragment : Fragment() {
    lateinit var binding: FragmentOrdersBinding

    private val adminOrdersViewModel: AdminOrdersViewModel by activityViewModels()
    lateinit var ordersAdapter: AdminOrdersAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrdersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvOrders.apply {
            layoutManager = LinearLayoutManager(requireContext())
            ordersAdapter = AdminOrdersAdapter()
            adapter = ordersAdapter
        }
        lifecycleScope.launch {
            adminOrdersViewModel.orders.collectLatest {
                when (it) {
                    is Resource.Failure -> {
                        binding.pbLoading.visibility = View.GONE
                    }

                    Resource.Loading -> {
                        binding.pbLoading.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        binding.pbLoading.visibility = View.GONE
                        ordersAdapter.submitList(it.data)
                    }

                    else -> {
                    }
                }
            }
        }

        ordersAdapter.orderClicked = {
            Intent(context, OrderdetailsActivity::class.java).apply {
                putExtra(OrderdetailsActivity.ORDER_KEY, it.orderId)
            }.also {
                startActivity(it)
            }
        }
    }
}