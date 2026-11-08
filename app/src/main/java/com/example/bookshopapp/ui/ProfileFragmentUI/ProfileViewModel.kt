package com.example.bookshopapp.ui.ProfileFragmentUI

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.remote.dto.OrderDTO
import com.example.bookshopapp.data.remote.dto.UserDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


class ProfileViewModel (application: android.app.Application): AndroidViewModel(application) {
    private val userRepository = (application as BookShopApplication).usersRepository
    private val orderRepository = (application as BookShopApplication).ordersRepository

    private val _userData = MutableLiveData<UserDTO?>(null)
    private val _error = MutableLiveData<String?>(null)
    private val _userIsAnAthorise = MutableLiveData(false)
    private val _profileLoading = MutableLiveData(false)
    private val _orderDataLoading = MutableLiveData(false)

    private val _orderData = MutableLiveData<Map<Int, OrderDTO>>(mapOf())

    val error: LiveData<String?> = _error
    val profileLoading: LiveData<Boolean> = _profileLoading

    val userData: LiveData<UserDTO?> = _userData
    val userIsAnAthorise: LiveData<Boolean> = _userIsAnAthorise

    val orderData: LiveData<Map<Int, OrderDTO>> = _orderData
    val orderDataLoading: LiveData<Boolean> = _orderDataLoading



    fun loadUserData() {
        viewModelScope.launch {
            _orderDataLoading.value = true

            supervisorScope {
                val orderDataDeferred = async { runCatching { orderRepository.getOrders() } }
                val userDataDeferred = async { runCatching { userRepository.getUserInfo() } }


                val orderDataResult = orderDataDeferred.await()
                val userDataResult =  userDataDeferred.await()

                userDataResult.onSuccess { _userData.value = it }
                orderDataResult.onSuccess { _orderData.value = it }

                if (userDataResult.exceptionOrNull() is AuthException || orderDataResult.exceptionOrNull() is AuthException) {
                    _userIsAnAthorise.value = true
                }

                if (userDataResult.exceptionOrNull() is InternetException || orderDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (userDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException || orderDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }
            _orderDataLoading.value = false
        }
    }

    fun loadOrderNextPage() {
        if ((_orderDataLoading.value ?: false)) {
            return
        }

        viewModelScope.launch {
            _orderDataLoading.value = true

            supervisorScope {
                val nextOrderPageDeferred = async { runCatching { orderRepository.getOrdersNextPage() } }

                val nextOrderPageResult = nextOrderPageDeferred.await()
                nextOrderPageResult.onSuccess { _orderData.value = (_orderData.value ?: mapOf()) + it }

                if (nextOrderPageResult.exceptionOrNull() is AuthException) {
                    _userIsAnAthorise.value = true
                }

                if (nextOrderPageResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (nextOrderPageResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }
            _orderDataLoading.value = false
        }
    }


    fun logout() {
        viewModelScope.launch {
            _orderDataLoading.value = true
            supervisorScope {
                val logoutDeferred = async { runCatching { userRepository.logout() } }
                val logoutResult = logoutDeferred.await()
                logoutResult.onSuccess { _userIsAnAthorise.value = true }

                if (logoutResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (logoutResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }

            }
            _profileLoading.value = false
        }
    }


    fun onErrorShow() {
        _error.value = null
    }

}