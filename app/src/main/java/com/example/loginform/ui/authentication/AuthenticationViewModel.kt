package com.example.loginform.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginform.Model.Customer
import com.example.loginform.Model.stringToCustomer
import com.example.loginform.data.PrefRepository
import com.example.loginform.data.Resource
import com.example.loginform.data.authentication.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val prefRepository: PrefRepository
) :
    ViewModel() {

    private val _loginFlow = MutableStateFlow<Resource<Customer>?>(null)
    val loginFlow = _loginFlow.asStateFlow()

    private val _signUpFlow = MutableStateFlow<Resource<Boolean>?>(null)
    val signUpFlow = _signUpFlow.asStateFlow()

    val currentUser: Customer?
        get() = authRepository.currentUser

    init {
        if (currentUser != null) {
            _loginFlow.value = Resource.Success(prefRepository.currentUser.stringToCustomer()!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch {
        _loginFlow.value = Resource.Loading
        val result = authRepository.login(email, password)
        _loginFlow.value = result
    }

    fun signUp(customer: Customer) = viewModelScope.launch {
        _signUpFlow.value = Resource.Loading
        val result = authRepository.signUp(customer)
        _signUpFlow.value = result
    }

}