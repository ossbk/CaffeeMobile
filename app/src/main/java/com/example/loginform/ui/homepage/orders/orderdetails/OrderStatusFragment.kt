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
import com.example.loginform.R
import com.example.loginform.databinding.FragmentOrderstatusTabfragmentBinding
import com.example.loginform.ui.homepage.orders.OrdersViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class OrderStatusFragment : Fragment() {
    private lateinit var binding: FragmentOrderstatusTabfragmentBinding

    val ordersvm: OrdersViewModel by activityViewModels()

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
    }


    fun changestatus(om: Order) {
        when (om.orderStatus) {
            OrderStatus.CONFIRMED -> {
                confirmed()
            }
            OrderStatus.PROCESSING->{
                preparingOrder()
            }

            OrderStatus.READY_TO_PICKUP -> {
                orderReady()
            }

            OrderStatus.CANCELLED -> {
                cancelled(om)
            }

            OrderStatus.COMPLETED -> {
                orderReady()
            }

            else -> {}
        }

    }

    fun cancelled(om: Order) {

        if (om.laststatus == OrderStatus.ORDERED) {
            binding.tvconfirm.text = om.orderStatus.status
            binding.desconfirm.text = "Your order has been cancelled"
            binding.imgConfirmed.setImageResource(R.drawable.ic_canceled)
            binding.v2.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_cancel_24
            )

            binding.tvdelivered.makeGone()
            binding.desdelivered.makeGone()
            binding.imgDelivered.makeGone()
            binding.v4.makeGone()
            binding.d3.makeGone()
            binding.tvdispatch.makeGone()
            binding.desdispatch.makeGone()
            binding.imgDispatched.makeGone()
            binding.v3.makeGone()
            binding.d2.makeGone()
        } else if (om.laststatus == OrderStatus.CONFIRMED) {
            confirmed()
            binding.tvdispatch.text = om.orderStatus.status
            binding.desdispatch.text = "Your order has been cancelled"
            binding.imgDispatched.setImageResource(R.drawable.ic_canceled)
            binding.v3.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_cancel_24
            )

            binding.tvdelivered.makeGone()
            binding.desdelivered.makeGone()
            binding.imgDelivered.makeGone()
            binding.v4.makeGone()
            binding.d3.makeGone()
        } else if (om.laststatus == OrderStatus.READY_TO_PICKUP) {
            preparingOrder()
            binding.tvdelivered.text = om.orderStatus.status
            binding.desdelivered.text = "Your order has been cancelled"
            binding.imgDelivered.setImageResource(R.drawable.ic_canceled)
            binding.v4.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_cancel_24
            )
        }


    }

    fun confirmed() {
        binding.v2.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
        binding.desconfirm.text = OrderStatus.CONFIRMED.msg
        binding.tvconfirm.text = OrderStatus.CONFIRMED.status

        binding.tvdispatch.alpha = 1f
        binding.desdispatch.alpha = 1f
        binding.imgDispatched.alpha = 1f
        binding.d2.alpha = 1f
        binding.d2.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_status_completed)
        binding.v3.alpha = 1f
        binding.v3.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrows)

    }

    fun preparingOrder() {
        binding.desdispatch.text = OrderStatus.READY_TO_PICKUP.status
        binding.tvdispatch.text = OrderStatus.READY_TO_PICKUP.msg

        confirmed()
        binding.v3.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
        binding.tvdelivered.alpha = 1f
        binding.desdelivered.alpha = 1f
        binding.imgDelivered.alpha = 1f

        binding.d3.alpha = 1f
        binding.d3.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.shape_status_completed)
        binding.v4.alpha = 1f
        binding.v4.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_arrows)

    }

    fun orderReady() {
        confirmed()
        preparingOrder()
        binding.desdelivered.text = OrderStatus.COMPLETED.msg

        binding.v4.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_check)
    }

}


fun View.makeGone() {
    this.visibility = View.GONE
}