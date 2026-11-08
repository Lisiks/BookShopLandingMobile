package com.example.bookshopapp.ui.AddCartActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.CartException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlin.math.round


class AddCartViewModel(application: Application): AndroidViewModel(application) {

    private val cartManager =
        (application as BookShopApplication).cartDataStoreManager


    private val _bookId = MutableLiveData<Int?>(null)
    private val _bookTitle = MutableLiveData<String?>(null)
    private val _bookPrice = MutableLiveData<Double?>(null)
    private val _bookCount = MutableLiveData<Int>(1)
    private val _error = MutableLiveData<String?>(null)

    private val _loading = MutableLiveData<Boolean>(false)
    private val _bookIsAdded = MutableLiveData<Boolean>(false)
    val error: LiveData<String?> = _error


    val bookId: LiveData<Int?> = _bookId
    val bookTitle: LiveData<String?> = _bookTitle
    val bookPrice: LiveData<Double?> = _bookPrice
    val bookCount: LiveData<Int> = _bookCount

    val bookIsAdded: LiveData<Boolean> = _bookIsAdded
    val loading: LiveData<Boolean> = _loading

    fun calculatePrice(): Double = (_bookCount.value ?: 0) * (_bookPrice.value ?: 0.0)

    fun upCount() {
        _bookCount.value = (_bookCount.value ?: 0) + 1
    }

    fun downCount() {
        if ((_bookCount.value ?: 1) > 1) {
            _bookCount.value = (_bookCount.value ?: 2) - 1
        }
    }

    fun addBook() {
        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val addCartItemDeferred = async { runCatching { cartManager.addCartItem(_bookId.value ?: 0, _bookCount.value ?: 0) } }
                val addCartItemResult = addCartItemDeferred.await()

                addCartItemResult.onSuccess {
                    _bookIsAdded.value = true
                }

                if (addCartItemResult.exceptionOrNull() is CartException) {
                    if (addCartItemResult.exceptionOrNull()?.message == "Item count must be greater than 0") {
                        _error.value = "Кол-во единиц товара должно быть больше 0"
                    } else {
                        _error.value = "Достигуто максимальное кол-во позиций для одного заказа (10). Пожалуйста, перейдите в корзину для настройки заказа"
                    }
                }

                if (addCartItemResult.exceptionOrNull() != null && addCartItemResult.exceptionOrNull() !is CartException) {
                    _error.value = "Непредвиденная ошибка при сохранении данных корзины"
                }
            }

            _loading.value = false
        }
    }


    fun setBookParams(bookId: Int, bookPrice: Double, bookTitle: String) {
        _bookId.value = bookId
        _bookTitle.value = bookTitle
        _bookPrice.value = bookPrice
    }

    fun onErrorShow() {
        _error.value = null
    }
}