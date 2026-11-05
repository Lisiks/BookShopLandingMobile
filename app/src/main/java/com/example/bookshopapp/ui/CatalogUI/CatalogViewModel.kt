package com.example.bookshopapp.ui.login


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.LoginException
import com.example.bookshopapp.data.remote.dto.BookDTO


import kotlinx.coroutines.launch


class CatalogViewModel (application: Application): AndroidViewModel(application) {
    private val repository = (application as BookShopApplication).booksRepository
    private val _loading = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)
    private var _bookData = MutableLiveData<Map<Int, BookDTO>>(mapOf())

    private val _bookFullLoaded = MutableLiveData<Boolean>(false)

    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error
    val bookData: LiveData<Map<Int, BookDTO>> = _bookData
    val bookFullLoaded: LiveData<Boolean> = _bookFullLoaded
 


    init {
        viewModelScope.launch {
            _loading.value = true
            try {
                val bookData = repository.getBooks()
                _bookData.value = bookData

            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } finally {
                _loading.value = false
            }
        }
    }

    fun getNextPageBooks() {
        viewModelScope.launch {
            if (_loading.value ?: false) {
                return@launch
            }

            _loading.value = true
            try {
                val bookData = repository.getBooksNextPage()
                println(repository.pageIsLastValue)
                if (repository.pageIsLastValue) {
                    println("Киниги переполнены")
                    _bookFullLoaded.value = true
                } else {
                    _bookData.value = bookData
                }



            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } finally {
                _loading.value = false
            }
        }
    }

    fun onErrorShow() {
        _error.value = null
    }

}