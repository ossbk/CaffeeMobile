package com.example.loginform.Model

data class OrderItem(
    val orderItemId: String = "",
    var prodId: String = "",
    var qty: Int = 0,
    var prodPrice: Double = 0.0,
    var prodName: String = "",
    var prodDes: String = "",
    var prodImgUrl: String = "",
    var rating:Float=0.0f
){
    fun calculatePrice(): Double {
        return qty * prodPrice
    }
}
