package com.example.loginform.data.cartrepository

import android.util.Log
import com.example.loginform.Model.CartItem
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository,
) :
    CartRepository {

    private val cartRef: CollectionReference = firestore.collection("Cart")

    override fun getCartItems(): Flow<Resource<List<CartItem>>> = callbackFlow {
        val cartListener = cartRef.whereEqualTo("userId", authRepository.currentUser!!.cusId)
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
                val cartItems = value?.toObjects(CartItem::class.java)

                trySend(Resource.Success(cartItems ?: emptyList())).isSuccess
            }
        awaitClose {
            Log.d("cvrr", "closign after await")
            cartListener.remove()
        }
    }

    override suspend fun addToCart(prodId: String, qty: Long) {
        val docPath = authRepository.currentUser!!.cusId + prodId
        val cartItemRef = cartRef.document(docPath)
        var cartItem = cartItemRef.get().await()
            .toObject(CartItem::class.java)
        if (cartItem != null) {
            val map = mapOf(
                "qty"
                        to
                        FieldValue.increment(qty)
            )
            cartItemRef.set(map, SetOptions.merge())
        } else {
            cartItem = CartItem(docPath, prodId, qty.toInt(), authRepository.currentUser!!.cusId)
            cartItemRef.set(cartItem, SetOptions.merge())
        }
    }


    override suspend fun removeFromCart(prodId: String, qty: Long) {
        val docPath = authRepository.currentUser!!.cusId + prodId
        val cartItemRef = cartRef.document(docPath)
        var cartItem = cartItemRef.get().await()
            .toObject(CartItem::class.java)
        if (cartItem != null) {
            val map = mapOf(
                "qty"
                        to
                        FieldValue.increment(-qty)
            )
            cartItemRef.set(map, SetOptions.merge())
        } else {
            cartItem = CartItem(docPath, prodId, qty.toInt(), authRepository.currentUser!!.cusId)
            cartItemRef.set(cartItem, SetOptions.merge())
        }
    }

    override suspend fun updateQty(cartModel: CartItem) {
        val cartItemRef = cartRef.document(cartModel.cartId)
        cartItemRef.set(cartModel, SetOptions.merge())
    }

    override suspend fun deleteItem(cartId: String) {
        cartRef.document(cartId).delete()
    }

    override fun clearCart() {
        cartRef.whereEqualTo("userId", authRepository.currentUser!!.cusId)
            .get().addOnSuccessListener {
                it.documents.forEach { doc ->
                    doc.reference.delete()
                }
            }.addOnFailureListener {
                // throw it
            }
    }

}