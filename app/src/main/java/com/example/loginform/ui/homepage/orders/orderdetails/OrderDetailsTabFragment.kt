package com.example.finalspicyfood.mui.orderdetails

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.loginform.Model.Order
import com.example.loginform.Model.OrderStatus
import com.example.loginform.Model.mRoundOff
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentDetailsTabFragmentBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.example.loginform.ui.homepage.orders.OrdersViewModel
import com.example.loginform.ui.homepage.orders.orderdetails.OrderItemAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OrderDetailsTabFragment : Fragment() {
    private lateinit var binding: FragmentDetailsTabFragmentBinding

    private lateinit var orderItemAdapter: OrderItemAdapter
    private val ordersViewModel: OrdersViewModel by activityViewModels()
    var itemOrder: Order? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailsTabFragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        setuprecyclerview()


        lifecycleScope.launch {
            ordersViewModel.clickedOrder.collectLatest {
                itemOrder = it
                if (it != null) {
                    binding.total.text = (it.orderTotal).mRoundOff()
                    orderItemAdapter.submitList(it.orderItems)

                    if (it.orderStatus == OrderStatus.COMPLETED) {
                        checkIfRated(it)
                        binding.rateNow.isEnabled = true
                    }
                }
            }
        }


        lifecycleScope.launch {
            ordersViewModel.ratingProduct.collectLatest {
                when (it) {
                    is Resource.Failure -> {
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    Resource.Loading -> {
                        ProgressDialogUtil.showProgressDialog(context)
                    }

                    is Resource.Success -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        productsRated()
                        Toast.makeText(context,"Thanks for your feedback",Toast.LENGTH_SHORT).show()

                    }

                    null -> {
                        ProgressDialogUtil.dismissProgressDialog()
                    }
                }
            }
        }


        binding.rateNow.setOnClickListener {
            if (orderItemAdapter.isAllProductsRated()) {
                itemOrder?.let {
                    ordersViewModel.rateOrder(it, orderItemAdapter.ratingHashMap)
                }
            } else
                Toast.makeText(requireContext(), "Please rate all products", Toast.LENGTH_SHORT)
                    .show()
        }


    }

    private fun productsRated() {
        binding.rateNow.visibility = View.GONE
        binding.tvButton.visibility = View.GONE

        val mList = orderItemAdapter.currentList.map {
            it.copy(rating = orderItemAdapter.ratingHashMap[it.prodId] ?: 0.0f)
        }
        orderItemAdapter.submitList(mList)
    }

    private fun checkIfRated(order: Order) {
        if (order.isRated) {
            binding.rateNow.visibility = View.GONE
            binding.tvButton.visibility = View.GONE
        }
    }

    private fun setuprecyclerview() {
        binding.cartRecyclerview.apply {
            orderItemAdapter = OrderItemAdapter()
            adapter = orderItemAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

    }

    private fun showsnackbar(s: String) {
        Snackbar.make(binding.root, s, Snackbar.LENGTH_SHORT).show()
    }

}