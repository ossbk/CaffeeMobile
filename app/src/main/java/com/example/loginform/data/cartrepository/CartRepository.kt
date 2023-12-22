package com.example.loginform.data.cartrepository

import com.example.loginform.Model.CartItem
import com.example.loginform.data.Resource
import kotlinx.coroutines.flow.Flow

interface CartRepository {
     fun getCartItems(): Flow<Resource<List<CartItem>>>

    suspend fun addToCart(prodId: String, qty: Long)
    suspend fun removeFromCart(prodId: String, qty: Long)
    suspend fun updateQty(cartModel:CartItem)
    suspend fun deleteItem(cartId:String)
    fun clearCart()
}