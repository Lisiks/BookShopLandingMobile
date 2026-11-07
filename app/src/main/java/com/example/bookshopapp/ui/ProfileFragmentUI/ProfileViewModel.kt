package com.example.bookshopapp.ui.ProfileFragmentUI

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.remote.dto.OrderDTO
import com.example.bookshopapp.data.remote.dto.UserDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


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
            try {
                val orderDataDeferred = async { orderRepository.getOrders() }
                val userDataDeferred = async { userRepository.getUserInfo() }

                _userData.value = userDataDeferred.await()
                _orderData.value = orderDataDeferred.await()

            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: AuthException) {
                _userIsAnAthorise.value = true
            } finally {

                _orderDataLoading.value = false
            }
        }
    }

    fun loadOrderNextPage() {
        if ((_orderDataLoading.value ?: false)) {
            return
        }

        viewModelScope.launch {
            _orderDataLoading.value = true
            try {
                val orderData = orderRepository.getOrdersNextPage()
                _orderData.value = (_orderData.value ?: mapOf()) + orderData

            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            }catch (e: AuthException) {
                    _userIsAnAthorise.value = true
            } finally {
                _orderDataLoading.value = false
            }
        }
    }


    fun logout() {
        viewModelScope.launch {
            _profileLoading.value = true
            try {
                userRepository.logout()
                _userIsAnAthorise.value = true
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } finally {
                _profileLoading.value = false
            }
        }
    }



    fun onErrorShow() {
        _error.value = null
    }

}