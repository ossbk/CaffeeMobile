package com.example.loginform.ui.homepage.cart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.Model.CartItem
import com.example.loginform.Model.CartProduct
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.cartrepository.CartRepository
import com.example.loginform.data.orders.OrdersRepository
import com.example.loginform.data.productsrepository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val ordersRepository: OrdersRepository
) : ViewModel() {

    private val _cartProducts: MutableStateFlow<Resource<List<CartProduct>>?> =
        MutableStateFlow(null)
    val cartProducts: StateFlow<Resource<List<CartProduct>>?> = _cartProducts.asStateFlow()

    private val _orderStatus: MutableStateFlow<Resource<Boolean>?> =
        MutableStateFlow(null)
    val orderStatus: StateFlow<Resource<Boolean>?> = _orderStatus.asStateFlow()

    init {
        viewModelScope.launch {
            _cartProducts.value = Resource.Loading
            cartRepository.getCartItems().collectLatest { resource ->
                when (resource) {
                    is Resource.Failure -> {
                        _cartProducts.value = Resource.Failure(resource.message)
                    }

                    Resource.Loading -> {
                        _cartProducts.value = Resource.Loading
                    }

                    is Resource.Success -> {
                        val cartProducts = resource.data.mapNotNull {
                            val product = productRepository.getProduct(it.prodId)
                            if (product != null) {
                                CartProduct(
                                    it.prodId,
                                    it.qty.toInt(),
                                    it.cartId,
                                    product.prodName,
                                    product.prodDes,
                                    product.prodImage,
                                    product.prodPrice,
                                )
                            } else
                                null
                        }

                        _cartProducts.value = Resource.Success(cartProducts)
                    }
                }
            }
        }
    }


    fun removeQty(cartProduct: CartProduct) {
        viewModelScope.launch {
            cartRepository.updateQty(
                CartItem(
                    cartProduct.cartId,
                    cartProduct.productId,
                    cartProduct.qty - 1,
                    authRepository.currentUser!!.cusId
                )
            )
        }
    }

    fun addQty(cartProduct: CartProduct) {
        viewModelScope.launch {
            cartRepository.updateQty(
                CartItem(
                    cartProduct.cartId,
                    cartProduct.productId,
                    cartProduct.qty + 1,
                    authRepository.currentUser!!.cusId
                )
            )
        }
    }

    fun deleteCartItem(cartId: String) {
        viewModelScope.launch {
            cartRepository.deleteItem(cartId)
        }
    }

    fun clearCart() {
        Log.d("cvrr","Clear cart called")
        viewModelScope.launch {
            cartRepository.clearCart()
        }
    }


    fun placeOrder(currentList: MutableList<CartProduct>) {
        viewModelScope.launch {
            _orderStatus.value = Resource.Loading
            _orderStatus.value = ordersRepository.placeOrder(currentList)
        }
    }


}