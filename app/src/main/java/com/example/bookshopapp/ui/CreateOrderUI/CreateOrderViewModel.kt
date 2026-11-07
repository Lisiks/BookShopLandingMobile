package com.example.bookshopapp.ui.CreateOrderUI

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.dto.ShopDTO
import com.example.bookshopapp.ui.CartUI.CartItem
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

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

        if ((_bookCartDataMap.value?.size ?: 0) == 0) {
            _error.value = "В данный момент ваша корзина пуста"
            return
        }

        viewModelScope.launch {
            _loading.value = true

            val itemList = _bookCartDataMap.value?.values?.map { bookData  ->
                mapOf("bookId" to bookData.bookId, "count" to bookData.bookCount)
            } ?: listOf()

            try {
                orderRepository.createOrder(selectedShop, itemList)
                cartManager.deleteCartAll()
                _orderCreated.value = true
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: AuthException) {
                _error.value = "Для создания заказа необходимо войти в аккаунт"

            } catch (e: NotFoundException) {
                _error.value = "В заказе присутствуют недоступные товары, пожалуйста, обновите корзину, перейдя на соответствующую вкладку"
            } finally {
                _loading.value = false

            }

        }
    }


    fun loadCartData() {
        viewModelScope.launch {
            _loading.value = true
            val savedCartData = cartManager.getCartData()

            try {
                val cartItemDataDeferred =
                    async { bookRepository.getBookByList(savedCartData.keys.toList()) }
                val shopsData = async { shopRepository.getShops() }

                val serverCartBookData = cartItemDataDeferred.await()
                val serverShopsData = shopsData.await()


                val newBookDataMap: MutableMap<Int, CartItem> = mutableMapOf()
                for (cartElementId in serverCartBookData.keys) {
                    newBookDataMap[cartElementId] = CartItem(
                        cartElementId,
                        serverCartBookData[cartElementId]?.title ?: "",
                        savedCartData[cartElementId] ?: 1,
                        serverCartBookData[cartElementId]?.price ?: 1.00
                    )
                }

                val newShopsDataMap: MutableMap<String, Int> = mutableMapOf("Выберите магазин" to 0)
                for (shopData in serverShopsData.values) {
                    newShopsDataMap["г. ${shopData.town}, ул. ${shopData.street} ${shopData.housing}"] = shopData.id
                }

                _shopsDataMap.value = newShopsDataMap
                _bookCartDataMap.value = newBookDataMap
                val invalidCartElementIds = (savedCartData.keys - serverCartBookData.keys).toList()

                if (invalidCartElementIds.isNotEmpty()) {
                    cartManager.deleteCartMany(invalidCartElementIds)
                    _error.value =
                        "Часть книг из вашей корзины более недоступно для заказа. Перейдите в корзину для обновления данных."
                }
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"

            } finally {
                _loading.value = false
            }
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