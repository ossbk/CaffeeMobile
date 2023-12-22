package com.example.loginform.data.authentication

import com.example.loginform.Model.Customer
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val prefRepository: PrefRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {
    val customersCollection = firebaseFirestore.collection("Users")
    override val currentUser: Customer?
        get() = prefRepository.currentUser.stringToCustomer()

    override suspend fun login(email: String, password: String): Resource<Customer> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val customer = getCustomer(email)
            if (customer != null) {
                prefRepository.currentUser = customer.customerToString() ?: ""
                Resource.Success(customer)
            } else
                Resource.Failure("Something went wrong")
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    suspend fun getCustomer(email: String): Customer? {
        return try {
            val customersSnapshot =
                customersCollection.whereEqualTo("cusEmail", email).get().await()
            if (customersSnapshot.isEmpty) {
                null
            } else {
                val customers = customersSnapshot.toObjects(Customer::class.java)
                customers[0]
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signUp(customer: Customer): Resource<Boolean> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(customer.cusEmail, customer.cusPassword)
                .await()
            Resource.Success(true)
            saveCustomerToFirebase(customer)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    private suspend fun saveCustomerToFirebase(customer: Customer): Resource<Boolean> {
        return try {
            val id = customersCollection.document().id
            customer.cusId = id
            customersCollection.document(id).set(customer).await()
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Failure(e.message.toString())
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
        prefRepository.currentUser = ""
    }
}