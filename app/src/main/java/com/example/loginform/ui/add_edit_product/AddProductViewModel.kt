package com.example.loginform.ui.add_edit_product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.Model.ProductItem
import com.example.loginform.data.Resource
import com.example.loginform.data.productsrepository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _addServiceResponse = MutableStateFlow<Resource<Boolean>?>(null)
    val addServiceResponse = _addServiceResponse.asStateFlow()
    fun addProduct(productItem: ProductItem) {
        viewModelScope.launch {
            _addServiceResponse.value = productRepository.addProduct(productItem)
        }
    }
}