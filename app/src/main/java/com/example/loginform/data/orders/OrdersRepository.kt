package com.example.loginform.data.orders

import com.example.loginform.Model.CartProduct
import com.example.loginform.Model.Order
import com.example.loginform.data.Resource
import kotlinx.coroutines.flow.Flow

interface OrdersRepository {

    suspend fun placeOrder(items: MutableList<CartProduct>): Resource<Boolean>
    fun getOrderItems(): Flow<Resource<List<Order>>>
    fun getAllOrderItems(): Flow<Resource<List<Order>>>
    suspend fun rateOrder(
        itemOrder: Order,
        ratingHashMap: HashMap<String, Float>
    ): Resource<Boolean>

     suspend fun updateOrderStatus(order: Order): Boolean


}