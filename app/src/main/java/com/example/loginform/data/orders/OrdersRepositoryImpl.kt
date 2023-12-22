package com.example.loginform.data.orders

import android.util.Log
import com.example.loginform.Model.CartProduct
import com.example.loginform.Model.Order
import com.example.loginform.Model.OrderItem
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.productsrepository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.HashMap
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository
) :
    OrdersRepository {

    val ordersRef = firestore.collection("Orders")
     override suspend fun placeOrder(items: MutableList<CartProduct>): Resource<Boolean> {
        return try {
            val orderId = ordersRef.document().id
            val total = items.sumOf {
                it.calculatePrice()
            }
            val order =
                Order(
                    orderId = orderId,
                    userId = authRepository.currentUser!!.cusId,
                    orderTotal = total
                )

            order.orderItems = items.map {
                OrderItem(
                    orderItemId = orderId,
                    prodId = it.productId,
                    qty = it.qty,
                    prodPrice = it.prodPrice,
                    prodName = it.prodName,
                    prodDes = it.prodDes,
                    prodImgUrl = it.prodImgUrl
                )
            }
            ordersRef.document(order.orderId).set(order).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }

    }

    override fun getOrderItems(): Flow<Resource<List<Order>>> = callbackFlow {
        val cartListener = ordersRef.whereEqualTo("userId", authRepository.currentUser!!.cusId)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("cvrr", "Message =${error.message}")
                    trySend(Resource.Failure(error.message.toString())).isSuccess
                    //   return@addSnapshotListener
                }
                if (value == null) {
                    Log.d("cvrr", "No data found")

                    trySend(Resource.Failure("No data found")).isSuccess
                    //   return@addSnapshotListener
                }
                val cartItems = value?.toObjects(Order::class.java)

                trySend(Resource.Success(cartItems ?: emptyList())).isSuccess
            }
        awaitClose {
            Log.d("cvrr", "closign after await")
            cartListener.remove()
        }
    }

    override suspend fun rateOrder(
        itemOrder: Order,
        ratingHashMap: HashMap<String, Float>
    ): Resource<Boolean> {
        itemOrder.isRated=true
        itemOrder.orderItems.forEach {
            it.rating=ratingHashMap[it.prodId]?:0.0f
        }

        return try {
            ordersRef.document(itemOrder.orderId).set(itemOrder).await()
            ratingHashMap.forEach { (key, value) ->
                productRepository.rateProduct(key,value)
            }
            Resource.Success(true)
        } catch (e:Exception){
            Resource.Failure(e.message.toString())
        }
    }


}