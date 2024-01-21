package com.example.loginform.ui

import com.example.loginform.Model.CartProduct
import com.example.loginform.Model.Customer
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.cartrepository.CartRepository
import com.example.loginform.data.orders.OrdersRepository
import com.example.loginform.data.productsrepository.ProductRepository
import com.example.loginform.ui.homepage.cart.CartViewModel
import com.example.loginform.ui.productdetails.ProductDetailsViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CartViewModelTest {
    private val authRepository: AuthRepository = mockk()
    private val productRepository: ProductRepository = mockk()
    private val ordersRepository: OrdersRepository = mockk()
    private val cartRepository: CartRepository = mockk()
    private lateinit var cartViewModel: CartViewModel

    @Before
    fun setup(){
        coEvery { authRepository.currentUser } returns Customer(cusId = "test-id")
        coEvery { cartRepository.getCartItems()  } returns emptyFlow()

        cartViewModel = CartViewModel(cartRepository, authRepository, productRepository, ordersRepository)
    }

    @Test
    fun `removeQty should update quantity in cart`() = runTest{
        coEvery { cartRepository.updateQty(any()) } just runs

        cartViewModel.removeQty(CartProduct("p-id"))

        coVerify { cartRepository.updateQty(any()) }
    }

    @Test
    fun `addQty should update quantity in cart`() = runTest{
        coEvery { cartRepository.updateQty(any()) } just runs

        cartViewModel.removeQty(CartProduct("p-id"))

        coVerify { cartRepository.updateQty(any()) }
    }

    @Test
    fun `deleteCartItem should delete item from cart`() = runTest{
        coEvery { cartRepository.deleteItem(any()) } just runs

        cartViewModel.deleteCartItem("cart-id")

        coVerify { cartRepository.deleteItem("cart-id") }
    }

    @Test
    fun `clearCart should clear items from cart`() = runTest{
        coEvery { cartRepository.clearCart() } just runs

        cartViewModel.clearCart()

        coVerify { cartRepository.clearCart() }
    }

    @Test
    fun `placeOrder should add items in order`() = runTest{
        coEvery { ordersRepository.placeOrder(any()) } returns Resource.Success(true)

        cartViewModel.placeOrder(mutableListOf(CartProduct("p-id")))

        assertTrue(cartViewModel.orderStatus.value is Resource.Success)
        assertEquals(Resource.Success(true), cartViewModel.orderStatus.value)

        coVerify { ordersRepository.placeOrder(any()) }
    }

}