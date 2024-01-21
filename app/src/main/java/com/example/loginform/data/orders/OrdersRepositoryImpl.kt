package com.example.loginform.data.orders

import android.util.Log
import com.easy_fcm.notifications.models.NotificationData
import com.easy_fcm.notifications.utils.FcmPushHelper
import com.example.loginform.Model.CartProduct
import com.example.loginform.Model.Order
import com.example.loginform.Model.OrderItem
import com.example.loginform.Model.OrderStatus
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.example.loginform.data.productsrepository.ProductRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrdersRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
    private val productRepository: ProductRepository,
    private val prefRepository: PrefRepository
) :
    OrdersRepository {

    val ordersRef = firestore.collection("Orders")
    val user = prefRepository.currentUser.stringToCustomer()!!
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
                    orderTotal = total,
                    userName = user.cusUserName,
                    userEmail = user.cusEmail
                )

            order.orderItems = items.map {
                OrderItem(
                    orderItemId = orderId,
                    prodId = it.productId,
                    qty = it.qty,
                    prodPrice = it.prodPrice,
                    prodName = it.prodName,
                    prodDes = it.prodDes,
                    prodImgUrl = it.prodImgUrl,
                )
            }
            val helper = FcmPushHelper.Builder()
                .setServerKey("AAAAxlmVYew:APA91bGSZuF8AuOEUfWL2FvFRJei3ATxdmrq7ZyW2NIIrB6CMof0Pq5zUhJq7oueohF5K7SbBWiiKWZGsrrHS5gdO2yJmstKJr9142W5CBwSDi8lE6kTz4Nfw37NuCHleTiCkJgtlELf")
                .setTokenOrTopic(
                    value = "admin",
                    isTopic = true
                )
                .setNotificationData(
                    notification = NotificationData(
                        "Order #${order.orderId}",
                        prefRepository.currentUser.stringToCustomer()!!.cusFullName + " has placed an order"
                    )
                )
                .build()
            helper.pushNotification(onSuccess = {
                Log.d("cvrr", "Notification sent $it")
            }, onError = {
                Log.d("cvrr", "Notification failed $it")

            }
            )
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

    override fun getAllOrderItems(): Flow<Resource<List<Order>>> = callbackFlow {
        val cartListener = ordersRef.addSnapshotListener { value, error ->
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

    override suspend fun updateOrderStatus(order: Order): Boolean {
        if (order.orderStatus == OrderStatus.ORDERED) {
            order.orderStatus = OrderStatus.PREPARING
        } else
            order.orderStatus = OrderStatus.COMPLETED

        val helper = FcmPushHelper.Builder()
            .setServerKey("AAAAxlmVYew:APA91bGSZuF8AuOEUfWL2FvFRJei3ATxdmrq7ZyW2NIIrB6CMof0Pq5zUhJq7oueohF5K7SbBWiiKWZGsrrHS5gdO2yJmstKJr9142W5CBwSDi8lE6kTz4Nfw37NuCHleTiCkJgtlELf")
            .setTokenOrTopic(
                value = prefRepository.currentUser.stringToCustomer()!!.cusId,
                isTopic = true
            )
            .setNotificationData(
                notification = NotificationData(
                    "Order #${order.orderId}",
                    order.orderStatus.msg
                )
            )
            .build()
        helper.pushNotification(onSuccess = {
            Log.d("cvrr", "Notification sent $it")
        }, onError = {
            Log.d("cvrr", "Notification failed $it")

        }
        )
        return try {
            ordersRef.document(order.orderId).set(order).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun rateOrder(
        itemOrder: Order,
        ratingHashMap: HashMap<String, Float>
    ): Resource<Boolean> {
        itemOrder.isRated = true
        itemOrder.orderItems.forEach {
            it.rating = ratingHashMap[it.prodId] ?: 0.0f
        }
        return try {
            ordersRef.document(itemOrder.orderId).set(itemOrder).await()
            ratingHashMap.forEach { (key, value) ->
                productRepository.rateProduct(key, value)
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }


}