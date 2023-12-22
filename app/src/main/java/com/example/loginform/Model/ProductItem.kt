package com.example.loginform.Model

data class ProductItem(
    var prodId: String = "",
    var prodName: String = "",
    var prodDes: String = "",
    var prodPrice: Double = 0.0,
    var prodImage: String = "",
    var prodRating: Int = 0,
    var prodNoOfPeopleRated: Int = 0,
    var prodAvailable: Boolean = false,
    var productType: ItemType = ItemType.CAKE
)

enum class ItemType {
    COFFEE,
    CAKE
}

