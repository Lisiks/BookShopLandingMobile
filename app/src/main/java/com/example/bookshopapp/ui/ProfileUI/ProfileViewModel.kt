package com.example.bookshopapp.ui.ProfileUI

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.LoginException
import com.example.bookshopapp.data.remote.dto.UserDTO
import kotlinx.coroutines.launch


class ProfileViewModel (application: android.app.Application): AndroidViewModel(application) {
    private val userRepository = (application as BookShopApplication).usersRepository
    private val orderRepository = (application as BookShopApplication).ordersRepository

    private val _userData = MutableLiveData<UserDTO?>(null)
    private val _error = MutableLiveData<String?>(null)
    private val _userIsAnAthorise = MutableLiveData(false)
    private val _logoutIsLoading = MutableLiveData(false)
    private val _orderDataIsLoading = MutableLiveData(false)

    init {
        viewModelScope.launch {
            try {
                _userData.value = userRepository.getUserInfo()
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: AuthException) {
                _userIsAnAthorise.value = true
            }
        }
    }

    val error: LiveData<String?> = _error
    val logoutLoading: LiveData<Boolean> = _logoutIsLoading
    val orderDataIsLoading: LiveData<Boolean> = _orderDataIsLoading
    val userData: LiveData<UserDTO?> = _userData
    val userIsAnAthorise: LiveData<Boolean> = _userIsAnAthorise


    fun logout() {
        viewModelScope.launch {
            _logoutIsLoading.value = true
            try {
                userRepository.logout()
                _userIsAnAthorise.value = true
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } finally {
                _logoutIsLoading.value = false
            }
        }
    }



    fun onErrorShow() {
        _error.value = null
    }

}