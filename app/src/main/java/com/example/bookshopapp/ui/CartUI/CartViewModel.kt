package com.example.bookshopapp.ui.CartUI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.InternetException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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
            supervisorScope {
                val savedCartDataDeferred = async { runCatching { cartManager.getCartData() } }
                val savedCartDataResult = savedCartDataDeferred.await()

                savedCartDataResult.onFailure {
                    _error.value = "Непредвиденная ошибка при чтении данных корзины"
                    return@supervisorScope
                }

                val savedCartData = savedCartDataResult.getOrNull() ?: mapOf()

                val bookDataDeferred = async { runCatching {  repository.getBookByList(savedCartData.keys.toList()) } }
                val bookDataResult = bookDataDeferred.await()

                bookDataResult.onSuccess {
                    val newBookDataMap: MutableMap<Int, CartItem> = mutableMapOf()
                    for (cartElementId in it.keys) {
                        newBookDataMap[cartElementId] = CartItem(
                            cartElementId,
                            it[cartElementId]?.title ?: "",
                            savedCartData[cartElementId] ?: 1,
                            it[cartElementId]?.price ?: 1.00
                        )

                    }
                    _bookCartDataMap.value = newBookDataMap
                }

                if (bookDataResult.isSuccess) {
                    val resultBookKeys = bookDataResult.getOrNull()?.keys ?: setOf()
                    val invalidCartElementIds = (savedCartData.keys - resultBookKeys).toList()

                    val deleteInvalidCartElementDeferred = async { runCatching { cartManager.deleteCartMany(invalidCartElementIds) } }
                    val deleteInvalidCartElementResult = deleteInvalidCartElementDeferred.await()
                    deleteInvalidCartElementResult.onFailure {
                        _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                        return@supervisorScope
                    }
                }

                if (bookDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (bookDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }

            _loading.value = false


        }
    }

    fun clearCartData() {
        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val clearCartDataDeferred = async { runCatching { cartManager.deleteCartAll() } }
                val clearCartDataResult = clearCartDataDeferred.await()

                clearCartDataResult.onSuccess {
                    _bookCartDataMap.value = mapOf()
                }

                clearCartDataResult.onFailure {
                    _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                    return@supervisorScope
                }
            }
            _loading.value = false
        }
    }

    fun deleteCartItem(itemId: Int) {
        if (_loading.value ?: false) {
            return
        }

        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val deleteCartItemDataDeferred = async { runCatching { cartManager.deleteCartItem(itemId) } }
                val deleteCartItemDataDeferredResult = deleteCartItemDataDeferred.await()

                deleteCartItemDataDeferredResult.onSuccess {
                    _bookCartDataMap.value = (_bookCartDataMap.value ?: mutableMapOf()) - itemId
                }

                deleteCartItemDataDeferredResult.onFailure {
                    _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                    return@supervisorScope
                }
            }
            _loading.value = false
        }

    }

    fun onErrorShow() {
        _error.value = null
    }
}