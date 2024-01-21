package com.example.loginform.Model

import android.net.Uri
import com.google.firebase.firestore.Exclude

data class ProductItem(
    var prodId: String = "",
    var prodName: String = "",
    var prodDes: String = "",
    var prodPrice: Double = 0.0,
    var prodImage: String = "",
    var prodRating: Int = 0,
    var prodNoOfPeopleRated: Int = 0,
    var prodAvailable: Boolean = false,
    @get:Exclude var imageUri: Uri? = null,
    var productType: ItemType = ItemType.CAKE,
    var ratings: ArrayList<UserRating> = arrayListOf()
)

enum class ItemType {
    COFFEE,
    CAKE
}

fun String.toItemType(): ItemType {
    return if (this.contains("cake", true)) {
        ItemType.CAKE
    } else
        ItemType.COFFEE
}

