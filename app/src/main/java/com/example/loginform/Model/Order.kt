package com.example.loginform.Model

import com.google.firebase.Timestamp

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val orderDate: Long = Timestamp.now().seconds,
    val orderStatus: OrderStatus = OrderStatus.ORDERED,
    val orderTotal: Double = 0.0,
    var orderItems: List<OrderItem> = listOf(),
    var isRated: Boolean = false,
    val laststatus:OrderStatus=OrderStatus.ORDERED
)

enum class OrderStatus(val status: String,val msg:String) {
    ORDERED("Order Placed","Your order has been placed"),
    CONFIRMED("Order Confirmed","Your order is confirmed"),
    PROCESSING("Order Processing","Your order is being processed"),
    READY_TO_PICKUP("Ready to Pickup","Your order is ready."),
    COMPLETED("Order Completed","Your order is completed successfully"),
    CANCELLED("Order Cancelled","Your order is cancelled")
}
