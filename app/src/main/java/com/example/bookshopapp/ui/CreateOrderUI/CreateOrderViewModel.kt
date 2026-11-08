package com.example.bookshopapp.ui.CreateOrderUI

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.dto.ShopDTO
import com.example.bookshopapp.ui.CartUI.CartItem
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class  CreateOrderViewModel (application: Application): AndroidViewModel(application) {
    private val cartManager =
        (application as BookShopApplication).cartDataStoreManager

    private val bookRepository =
        (application as BookShopApplication).booksRepository

    private val orderRepository =
        (application as BookShopApplication).ordersRepository

    private val shopRepository =
        (application as BookShopApplication).shopRepository


    private val _loading = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)
    private val _shopsDataMap = MutableLiveData<Map<String, Int>>(mapOf())
    private val _bookCartDataMap = MutableLiveData<Map<Int, CartItem>>(mutableMapOf())

    private val _selectedShop = MutableLiveData(0)

    private val _orderCreated = MutableLiveData<Boolean>(false)

    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error
    val bookCartDataMap: LiveData<Map<Int, CartItem>> = _bookCartDataMap
    val shopsDataMap: LiveData<Map<String, Int>> = _shopsDataMap

    val selectedShop: LiveData<Int> = _selectedShop

    val orderCreated: LiveData<Boolean> = _orderCreated


    fun setSelectedShop(shopString: String) {
        _selectedShop.value = _shopsDataMap.value?.get(shopString) ?: 0
    }

    fun createOrder() {
        val selectedShop = selectedShop.value ?: 0

        if (selectedShop == 0) {
            _error.value = "Выберите магазин заказа"
            return
        }

        val itemList = _bookCartDataMap.value?.values?.map { bookData  ->
            mapOf("bookId" to bookData.bookId, "count" to bookData.bookCount)
        } ?: listOf()

        if (itemList.isEmpty()) {
            _error.value = "В данный момент ваша корзина пуста"
            return
        }

        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val createOrderDeferred = async { runCatching { orderRepository.createOrder(selectedShop, itemList) } }
                val createOrderResult = createOrderDeferred.await()

                if (createOrderResult.isSuccess) {
                    val clearCartDeferred = async { runCatching { cartManager.deleteCartAll() } }
                    clearCartDeferred.await()
                    _orderCreated.value = true
                }

                if (createOrderResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (createOrderResult.exceptionOrNull() is AuthException) {
                    _error.value = "Для создания заказа необходимо войти в аккаунт"
                }

                if (createOrderResult.exceptionOrNull() is NotFoundException) {
                    _error.value = "В заказе присутствуют недоступные товары, пожалуйста, обновите корзину, перейдя на соответствующую вкладку"
                }

                if (createOrderResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }

            }
            _loading.value = false
        }
    }


    fun loadCartData() {
        viewModelScope.launch {
            _loading.value = true


            supervisorScope {
                val cartDataDeferred = async { runCatching { cartManager.getCartData() }}
                val cartDataResult = cartDataDeferred.await()

                cartDataResult.onFailure {
                    _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                    return@supervisorScope
                }

                val savedCartData = cartDataResult.getOrNull() ?: mapOf()

                val cartItemDataDeferred = async { runCatching { bookRepository.getBookByList(savedCartData.keys.toList()) } }
                val shopsDataDeferred = async { runCatching { shopRepository.getShops() } }

                val cartItemDataResult = cartItemDataDeferred.await()
                val shopsDataResult = shopsDataDeferred.await()

                cartItemDataResult.onSuccess {
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

                if (cartItemDataResult.isSuccess) {
                    val resultBookKeys = cartItemDataResult.getOrNull()?.keys ?: setOf()
                    val invalidCartElementIds = (savedCartData.keys - resultBookKeys).toList()

                    val deleteInvalidCartElementDeferred = async { runCatching { cartManager.deleteCartMany(invalidCartElementIds) } }
                    val deleteInvalidCartElementResult = deleteInvalidCartElementDeferred.await()

                    deleteInvalidCartElementResult.onFailure {
                        _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                        return@supervisorScope
                    }
                }

                shopsDataResult.onSuccess {
                    val newShopsDataMap: MutableMap<String, Int> = mutableMapOf("Выберите магазин" to 0)
                    for (shopData in it.values) {
                        newShopsDataMap["г. ${shopData.town}, ул. ${shopData.street} ${shopData.housing}"] = shopData.id
                    }
                    _shopsDataMap.value = newShopsDataMap
                }

                if (cartItemDataResult.exceptionOrNull() is InternetException || shopsDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (cartItemDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException || shopsDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }

            _loading.value = false
        }
    }



    fun calculateCartPrice(): Double = _bookCartDataMap.value?.values?.toList()?.fold(0.0)
        { acc, item ->
            acc + item.bookPrice * item.bookCount
        } ?: 0.0


    fun onErrorShow() {
        _error.value = null
    }
}