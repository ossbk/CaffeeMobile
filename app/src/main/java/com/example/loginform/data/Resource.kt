package com.example.loginform.data

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Failure(val message: String) : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
}