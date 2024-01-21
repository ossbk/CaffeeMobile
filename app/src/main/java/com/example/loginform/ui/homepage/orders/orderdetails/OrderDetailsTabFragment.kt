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
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentDetailsTabFragmentBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import com.example.loginform.ui.homepage.orders.AdminOrdersViewModel
import com.example.loginform.ui.homepage.orders.orderdetails.OrderItemAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.integrity.internal.t
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class OrderDetailsTabFragment : Fragment() {
    private lateinit var binding: FragmentDetailsTabFragmentBinding

    private lateinit var adminOrderItemAdapter: OrderItemAdapter
    private val adminOrdersViewModel: AdminOrdersViewModel by activityViewModels()
    var itemOrder: Order? = null

    @Inject
    lateinit var prefRepository: PrefRepository
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
            adminOrdersViewModel.clickedOrder.collectLatest {
                itemOrder = it
                if (it != null) {
                    binding.total.text = (it.orderTotal).mRoundOff()
                    adminOrderItemAdapter.submitList(it.orderItems)

                    if (it.orderStatus == OrderStatus.COMPLETED) {
                        checkIfRated(it)
                        binding.rateNow.isEnabled = true
                    }
                }
            }
        }


        lifecycleScope.launch {
            adminOrdersViewModel.ratingProduct.collectLatest {
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
                        Toast.makeText(context, "Thanks for your feedback", Toast.LENGTH_SHORT)
                            .show()

                    }

                    null -> {
                        ProgressDialogUtil.dismissProgressDialog()
                    }
                }
            }
        }


        binding.rateNow.setOnClickListener {
            if (adminOrderItemAdapter.isAllProductsRated()) {
                itemOrder?.let {
                    adminOrdersViewModel.rateOrder(it, adminOrderItemAdapter.ratingHashMap)
                }
            } else
                Toast.makeText(requireContext(), "Please rate all products", Toast.LENGTH_SHORT)
                    .show()
        }

        if (prefRepository.currentUser.stringToCustomer()?.cusIsAdmin == true) {
            binding.bottomBar.visibility = View.GONE
        }


    }

    private fun productsRated() {
        binding.rateNow.visibility = View.GONE
        binding.tvButton.visibility = View.GONE

        val mList = adminOrderItemAdapter.currentList.map {
            it.copy(rating = adminOrderItemAdapter.ratingHashMap[it.prodId] ?: 0.0f)
        }
        adminOrderItemAdapter.submitList(mList)
    }

    private fun checkIfRated(order: Order) {
        if (order.isRated) {
            binding.rateNow.visibility = View.GONE
            binding.tvButton.visibility = View.GONE
        }
    }

    private fun setuprecyclerview() {
        binding.cartRecyclerview.apply {
            adminOrderItemAdapter = OrderItemAdapter(prefRepository.currentUser.stringToCustomer()?.cusIsAdmin== true)
            adapter = adminOrderItemAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }

    }

    private fun showsnackbar(s: String) {
        Snackbar.make(binding.root, s, Snackbar.LENGTH_SHORT).show()
    }

}