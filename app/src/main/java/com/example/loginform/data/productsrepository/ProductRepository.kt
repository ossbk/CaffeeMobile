package com.example.loginform.data.productsrepository

import com.example.loginform.Model.ProductItem
import com.example.loginform.data.Resource

interface ProductRepository {
    suspend fun getCoffeeItems(): Resource<List<ProductItem>>
    suspend fun getCakeItems(): Resource<List<ProductItem>>
    suspend fun getProduct(prodId: String): ProductItem?
    fun rateProduct(key: String, value: Float)
   suspend fun searchProducts(query:String): Resource<List<ProductItem>>

    var selectedProduct: ProductItem?
}