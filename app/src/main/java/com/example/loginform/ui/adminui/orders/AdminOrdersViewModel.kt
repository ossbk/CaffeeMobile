package com.example.loginform.ui.homepage.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.Model.Order
import com.example.loginform.data.Resource
import com.example.loginform.data.orders.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminOrdersViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository
) : ViewModel() {

    private val _orders: MutableStateFlow<Resource<List<Order>>?> = MutableStateFlow(null)
    val orders: StateFlow<Resource<List<Order>>?> = _orders.asStateFlow()

    private val _ratingProduct: MutableStateFlow<Resource<Boolean>?> = MutableStateFlow(null)
    val ratingProduct: StateFlow<Resource<Boolean>?> = _ratingProduct.asStateFlow()

    private val _clickedOrder: MutableStateFlow<Order?> = MutableStateFlow(null)
    val clickedOrder: StateFlow<Order?> = _clickedOrder.asStateFlow()

    var clickedId = ""

    init {
        viewModelScope.launch {
            _orders.value = Resource.Loading
            ordersRepository.getAllOrderItems().collectLatest { listResource ->
                _orders.value = listResource

                if (listResource is Resource.Success && clickedId.isNotEmpty()) {
                    _clickedOrder.value =
                        listResource.data.first { it.orderId == clickedId }
                }
            }
        }
    }

    fun setSelectedOrder(orderId: String) {
        clickedId = orderId
        if (orders.value is Resource.Success && orderId.isNotEmpty())
            _clickedOrder.value =
                (orders.value as Resource.Success<List<Order>>).data.first { it.orderId == orderId }
    }

    fun rateOrder(itemOrder: Order, ratingHashMap: HashMap<String, Float>) {
        viewModelScope.launch {
            _ratingProduct.value = Resource.Loading
            _ratingProduct.value = ordersRepository.rateOrder(itemOrder, ratingHashMap)
        }
    }

    suspend fun updateOrderStatus(value: Order): Boolean {
        return ordersRepository.updateOrderStatus(value)
    }


}