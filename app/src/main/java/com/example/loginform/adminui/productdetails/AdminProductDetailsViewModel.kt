package com.example.loginform.ui.productdetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.data.cartrepository.CartRepository
import com.example.loginform.data.productsrepository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminProductDetailsViewModel @Inject constructor(
    private val productRepository: ProductRepository, private val cartRepository: CartRepository
) : ViewModel() {
    val selectedProduct
        get() = productRepository.selectedProduct


    private val _quantityFlow: MutableStateFlow<Int> = MutableStateFlow(1)
    val quantityFlow = _quantityFlow.asStateFlow()

    fun incQuantity() {
        _quantityFlow.update {
            it + 1
        }
    }

    fun decQuantity() {
        _quantityFlow.update {
            if (it == 1) {
                it
            } else it - 1
        }
    }

    fun addToCart(prodId: String, qty: Long) {
        viewModelScope.launch {
            cartRepository.addToCart(prodId, qty)
        }
    }


}