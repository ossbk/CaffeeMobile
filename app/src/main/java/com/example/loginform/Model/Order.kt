package com.example.loginform.Model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val orderDate: Long = Timestamp.now().seconds,
    var orderStatus: OrderStatus = OrderStatus.ORDERED,
    val orderTotal: Double = 0.0,
    var orderItems: List<OrderItem> = listOf(),
    var isRated: Boolean = false,
    val laststatus: OrderStatus = OrderStatus.ORDERED,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
)

enum class OrderStatus(val status: String, val msg: String) {
    ORDERED("Order Placed", "Your order has been placed"),
    PREPARING("Preparing Order", "Your order is being Prepared"),
    COMPLETED("Collect Order", "Your order is prepared, You can collect it."),
}
