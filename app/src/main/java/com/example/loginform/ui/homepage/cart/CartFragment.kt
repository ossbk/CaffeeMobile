package com.example.loginform.ui.homepage.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loginform.data.Resource
import com.example.loginform.databinding.FragmentCartBinding
import com.example.loginform.ui.dialogs.ProgressDialogUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {

    lateinit var binding: FragmentCartBinding
    lateinit var cartAdapter: CartAdapter

    val cartViewModel: CartViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext())
            cartAdapter = CartAdapter()
            adapter = cartAdapter
        }

        with(binding) {
            clearBasket.setOnClickListener {
                cartViewModel.clearCart()
            }

            btnCheckout.setOnClickListener {
                if (cartAdapter.currentList.isEmpty()) {
                    Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
                } else
                    cartViewModel.placeOrder(cartAdapter.currentList)
            }
        }


        lifecycleScope.launch {
            cartViewModel.orderStatus.collectLatest {
                when (it) {
                    is Resource.Failure -> {
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    Resource.Loading -> {
                        context?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                    }

                    is Resource.Success -> {
                        Toast.makeText(context,"Your order is placed.",Toast.LENGTH_SHORT).show()
                        ProgressDialogUtil.dismissProgressDialog()
                        cartViewModel.clearCart()
                    }

                    null -> {

                    }
                }
            }
        }

        lifecycleScope.launch {
            cartViewModel.cartProducts.collectLatest { resource ->
                when (resource) {
                    is Resource.Failure -> {
                        ProgressDialogUtil.dismissProgressDialog()
                    }

                    Resource.Loading -> {
                        context?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
                    }

                    is Resource.Success -> {
                        ProgressDialogUtil.dismissProgressDialog()
                        cartAdapter.submitList(resource.data)
                        binding.tvTotalCartPrice.text =
                            "$${resource.data.sumOf { it.prodPrice * it.qty }.formatTwoDecimal()}"
                    }

                    null -> {

                    }
                }
            }
        }

        cartAdapter.minusClicked = {
            context?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }
            cartViewModel.removeQty(it)
        }
        cartAdapter.plusClicked = {
            context?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }

            Log.d("cvrr", "plus is clicked")
            cartViewModel.addQty(it)
        }
        cartAdapter.deleteClicked = {
            context?.let { it1 -> ProgressDialogUtil.showProgressDialog(it1) }

            cartViewModel.deleteCartItem(it.cartId)
        }


    }
}

private fun Double.formatTwoDecimal(): String {
    return "%.2f".format(this)
}
