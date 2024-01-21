package com.example.finalspicyfood.mui.orderdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.loginform.Model.Order
import com.example.loginform.Model.OrderStatus
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.R
import com.example.loginform.data.PrefRepository
import com.example.loginform.databinding.FragmentOrderstatusTabfragmentBinding
import com.example.loginform.ui.add_edit_product.showToast
import com.example.loginform.ui.homepage.orders.AdminOrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class OrderStatusFragment : Fragment() {
    private lateinit var binding: FragmentOrderstatusTabfragmentBinding

    val ordersvm: AdminOrdersViewModel by activityViewModels()

    @Inject
    lateinit var prefRepository: PrefRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderstatusTabfragmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            ordersvm.clickedOrder.collectLatest {
                if (it != null) {
                    changestatus(it)
                }
            }
        }

        if (prefRepository.currentUser.stringToCustomer()?.cusIsAdmin == true) {
            binding.btnUpdateStatus.visibility = View.VISIBLE
        }
        binding.btnUpdateStatus.setOnClickListener {
            lifecycleScope.launch {
                ordersvm.clickedOrder.value?.let { it1 ->
                    val isSuccess=ordersvm.updateOrderStatus(it1)
                    if (isSuccess){
                        changestatus(it1)
                        context?.showToast("Order Status Updated Successfully")
                    }
                    else
                        context?.showToast("Something went wrong")

                }
            }
        }
    }



    fun changestatus(om: Order) {
        when (om.orderStatus) {
            OrderStatus.PREPARING -> {
                confirmed()
                binding.btnUpdateStatus.text="Collect Order"
            }
            OrderStatus.COMPLETED -> {
                orderReady()
                binding.btnUpdateStatus.visibility=View.GONE
            }

            else -> {}
        }

    }


    fun confirmed() {
        binding.v2.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
        binding.desconfirm.text = OrderStatus.PREPARING.msg
        binding.tvconfirm.text = OrderStatus.PREPARING.status

        binding.tvdelivered.alpha = 1f
        binding.desdelivered.alpha = 1f
        binding.imgDelivered.alpha = 1f
        binding.d2.alpha = 1f
        binding.d2.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_status_completed)
        binding.v4.alpha = 1f
        binding.v4.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrows)

    }


    private fun orderReady() {
        confirmed()
         binding.desdelivered.text = OrderStatus.COMPLETED.msg

        binding.v4.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
    }

}


fun View.makeGone() {
    this.visibility = View.GONE
}