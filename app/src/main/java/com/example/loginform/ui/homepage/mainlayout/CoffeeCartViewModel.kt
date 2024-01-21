package com.example.loginform.ui.homepage.mainlayout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.Model.ItemType
import com.example.loginform.Model.ProductItem
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.productsrepository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CoffeeCartViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) :
    ViewModel() {
    private val _coffeesState = MutableStateFlow<Resource<List<ProductItem>>?>(null)
    val coffeesState = _coffeesState.asStateFlow()

    private val _cakesState = MutableStateFlow<Resource<List<ProductItem>>?>(null)
    val cakesState = _cakesState.asStateFlow()

    var _selectedType = MutableStateFlow(ItemType.COFFEE)
    var selectedItemType = _selectedType.asStateFlow()


    fun setSelectedType(itemType: ItemType) {
        _selectedType.value = itemType
    }

    init {
        getCoffeeProducts()
        getCakesProducts()
    }

    fun getCoffeeProducts() = viewModelScope.launch {
        _coffeesState.value = Resource.Loading
        val result = productRepository.getCoffeeItems()
        delay(1)
        _coffeesState.value = result

    }

    fun getCakesProducts() = viewModelScope.launch {
        _cakesState.value = Resource.Loading
        val result = productRepository.getCakeItems()
        delay(1)
        _cakesState.value = result

    }

    fun reloadProducts() {
        getCoffeeProducts()
        getCakesProducts()
    }

    fun setSelectedProduct(it: ProductItem) {
        productRepository.selectedProduct = it
    }


    fun getSelectedFlow(itemType: ItemType): StateFlow<Resource<List<ProductItem>>?> {
        return if (itemType == ItemType.COFFEE)
            coffeesState
        else
            cakesState
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    suspend fun deleteProduct(it: ProductItem): Resource<Boolean> {
        return productRepository.deleteProduct(it)

    }


}