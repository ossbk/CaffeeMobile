package com.example.loginform.Model

data class CartItem(
    var cartId: String = "",
    var prodId: String = "",
    var qty: Int = 0,
    var userId: String = ""
)