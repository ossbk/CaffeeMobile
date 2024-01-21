package com.example.loginform.ui

import com.example.loginform.Model.Customer
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.cartrepository.CartRepository
import com.example.loginform.data.productsrepository.ProductRepository
import com.example.loginform.ui.authentication.AuthenticationViewModel
import com.example.loginform.ui.productdetails.ProductDetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProductDetailsViewModelTest {
    private val productRepository: ProductRepository = mockk()
    private val cartRepository: CartRepository = mockk()
    private lateinit var productViewModel: ProductDetailsViewModel

    @Before
    fun setup(){
        productViewModel = ProductDetailsViewModel(productRepository, cartRepository)
    }

    @Test
    fun `incQuantity should add quantity by 1`() = runTest{
        assertEquals(1, productViewModel.quantityFlow.value)

        productViewModel.incQuantity()

        val stateValue = productViewModel.quantityFlow.value

        assertEquals(2, stateValue)
    }

    @Test
    fun `decQuantity should subtract quantity by -1`() = runTest{
        productViewModel.incQuantity()

        assertEquals(2, productViewModel.quantityFlow.value)

        productViewModel.decQuantity()

        val stateValue = productViewModel.quantityFlow.value

        assertEquals(1, stateValue)
    }

    @Test
    fun `decQuantity should not update quantity when 1`() = runTest{
        assertEquals(1, productViewModel.quantityFlow.value)

        productViewModel.decQuantity()

        val stateValue = productViewModel.quantityFlow.value

        assertEquals(1, stateValue)
    }

    @Test
    fun `addToCart should add item to cart repository`() = runTest{
        coEvery { cartRepository.addToCart(any(), any()) } just runs

        productViewModel.addToCart("p-id", 4)

        coVerify { cartRepository.addToCart("p-id", 4) }
    }

}