package com.example.loginform.Model

data class CartProduct(
    val productId: String = "",
    val qty: Int = 0,
    val cartId: String = "",
    val prodName: String = "",
    val prodDes: String = "",
    val prodImgUrl: String = "",
    val prodPrice: Double = 0.0
) {
    fun totalPrice(): String {
        return calculatePrice().mRoundOff()
    }

    fun calculatePrice(): Double {
        return qty * prodPrice
    }
}

fun Double.mRoundOff(): String {
    return "%.2f".format(this)
}