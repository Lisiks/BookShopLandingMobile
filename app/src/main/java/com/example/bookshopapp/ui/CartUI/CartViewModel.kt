package com.example.bookshopapp.ui.CartUI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.InternetException
import kotlinx.coroutines.launch
import kotlin.math.round


class CartViewModel (application: Application): AndroidViewModel(application) {

    private val cartManager =
        (application as BookShopApplication).cartDataStoreManager

    private val repository =
        (application as BookShopApplication).booksRepository


    private val _bookCartDataMap = MutableLiveData<Map<Int, CartItem>>(mutableMapOf())
    private val _loading = MutableLiveData<Boolean>(false)
    private val _error = MutableLiveData<String?>(null)

    val bookCartDataMap: LiveData<Map<Int, CartItem>> = _bookCartDataMap
    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error


    fun calculateCartPrice(): Double = _bookCartDataMap.value?.values?.toList()?.fold(0.0)
        { acc, item ->
            acc + item.bookPrice * item.bookCount
        } ?: 0.0



    fun loadCartData() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val savedCartData = cartManager.getCartData()

                val serverCartBookData = repository.getBookByList(savedCartData.keys.toList())

                val newBookDataMap: MutableMap<Int, CartItem> = mutableMapOf()

                for (cartElementId in serverCartBookData.keys) {
                    newBookDataMap[cartElementId] = CartItem(
                        cartElementId,
                        serverCartBookData[cartElementId]?.title ?: "",
                        savedCartData[cartElementId] ?: 1,
                        serverCartBookData[cartElementId]?.price ?: 1.00
                    )

                }
                _bookCartDataMap.value = newBookDataMap

                val invalidCartElementIds = (savedCartData.keys - serverCartBookData.keys).toList()
                cartManager.deleteCartMany(invalidCartElementIds)
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } finally {
                _loading.value = false
            }

        }
    }

    fun clearCartData() {
        viewModelScope.launch {
            _loading.value = true
            cartManager.deleteCartAll()
            _bookCartDataMap.value = mapOf()
            _loading.value = false
        }
    }

    fun deleteCartItem(itemId: Int) {
        if (_loading.value ?: false) {
            return
        }

        viewModelScope.launch {
            _loading.value = true
            cartManager.deleteCartItem(itemId)
            _bookCartDataMap.value = (_bookCartDataMap.value ?: mutableMapOf()) - itemId
            _loading.value = false
        }

    }

    fun onErrorShow() {
        _error.value = null
    }
}