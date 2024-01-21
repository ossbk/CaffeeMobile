package com.example.loginform.Model

import com.google.gson.Gson

data class Customer(
    var cusId: String = "",
    var cusFullName: String = "",
    var cusEmail: String = "",
    var cusPhoneNo: String = "",
    var cusUserName: String = "",
    var cusPassword: String = "",
    var cusIsActive: Boolean = true
) {
    fun customerToString(): String {
        return try {
            Gson().toJson(this)
        } catch (e:Exception){
            ""
        }
    }


}

fun String.stringToCustomer(): Customer? {
    return try {
        Gson().fromJson(this, Customer::class.java)
    } catch (e: Exception) {
        null
    }
}