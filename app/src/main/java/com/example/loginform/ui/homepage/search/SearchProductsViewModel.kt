package com.example.loginform.ui.homepage.search

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
class SearchProductsViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _searchedResult: MutableStateFlow<Resource<List<ProductItem>>?> =
        MutableStateFlow(null)

    val searchResult = _searchedResult.asStateFlow()
    fun searchProducts(query: String){
        _searchedResult.value = Resource.Loading
        viewModelScope.launch {
            _searchedResult.value = productRepository.searchProducts(query)
        }
    }

    fun setSelectedProduct(it: ProductItem) {
        productRepository.selectedProduct=it
    }
}