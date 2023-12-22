package com.example.loginform.data.authentication

import com.example.loginform.Model.Customer
import com.example.loginform.data.Resource

interface AuthRepository {
    val currentUser: Customer?
    suspend fun login(email: String, password: String): Resource<Customer>
    suspend fun signUp(customer: Customer): Resource<Boolean>
    fun logout()
}