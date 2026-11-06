package com.example.bookshopapp.ui.AddCartActivity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import kotlinx.coroutines.launch
import kotlin.math.round


class AddCartViewModel(application: Application): AndroidViewModel(application) {

    private val cartManager =
        (application as BookShopApplication).cartDataStoreManager


    private val _bookId = MutableLiveData<Int?>(null)
    private val _bookTitle = MutableLiveData<String?>(null)
    private val _bookPrice = MutableLiveData<Double?>(null)
    private val _bookCount = MutableLiveData<Int>(1)

    private val _loading = MutableLiveData<Boolean>(false)
    private val _bookIsAdded = MutableLiveData<Boolean>(false)


    val bookId: LiveData<Int?> = _bookId
    val bookTitle: LiveData<String?> = _bookTitle
    val bookPrice: LiveData<Double?> = _bookPrice
    val bookCount: LiveData<Int> = _bookCount

    val bookIsAdded: LiveData<Boolean> = _bookIsAdded
    val loading: LiveData<Boolean> = _loading



    fun calculatePrice(): Double {
        return round((_bookCount.value ?: 0) * (_bookPrice.value ?: 0.0) * 100) / 100
     }

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

            try {
                cartManager.addCartItem(_bookId.value ?: 0, _bookCount.value ?: 0)
                _bookIsAdded.value = true
            } finally {
                _loading.value = false
            }
        }
    }


    fun setBookParams(bookId: Int, bookPrice: Double, bookTitle: String) {
        _bookId.value = bookId
        _bookTitle.value = bookTitle
        _bookPrice.value = bookPrice
    }
}