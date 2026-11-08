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
import kotlinx.coroutines.async


import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


class CatalogViewModel (application: Application): AndroidViewModel(application) {
    private val repository = (application as BookShopApplication).booksRepository
    private val _loading = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)
    private val _bookData = MutableLiveData<Map<Int, BookDTO>>(mapOf())

    private val _title = MutableLiveData<String?>(null)
    private val _minPrice = MutableLiveData<Double?>(null)
    private val _maxPrice = MutableLiveData<Double?>(null)
    private val _authorId = MutableLiveData<Int?>(null)
    private val _genreId = MutableLiveData<Int?>(null)


    fun setTitle(title: String?) {
        _title.value = title
    }

    fun setMinPrice(minPrice: Double?) {
        _minPrice.value = minPrice
    }

    fun setMaxPrice(maxPrice: Double?) {
        _maxPrice.value = maxPrice
    }

    fun setAuthorId(authorId: Int?) {
        _authorId.value = authorId
    }

    fun setGenreId(genreId: Int?) {
        _genreId.value = genreId
    }



    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error
    val bookData: LiveData<Map<Int, BookDTO>> = _bookData

    val title: LiveData<String?> = _title
    val minPrice: LiveData<Double?> = _minPrice
    val maxPrice: LiveData<Double?> = _maxPrice
    val authorId: LiveData<Int?> = _authorId
    val genreId: LiveData<Int?> = _genreId



    fun searchOnResume() {
        if ((bookData.value?.size ?: 0) != 0) {
            return
        }

        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val bookFirstPageDeferred = async { runCatching { repository.getBooks(
                    _title.value, _authorId.value, genreId.value, minPrice.value, maxPrice.value
                    ) } }

                val bookSearchResult = bookFirstPageDeferred.await()

                bookSearchResult.onSuccess {
                    _bookData.value = it
                }

                if (bookSearchResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }
                if (bookSearchResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Произошла непредвиденная ошибка"
                }

            }

            _loading.value = false
        }
    }



    fun getFirstBookPage() {
        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val bookFirstPageDeferred = async { runCatching { repository.getBooks(
                    _title.value, _authorId.value, genreId.value, minPrice.value, maxPrice.value
                ) } }

                val bookSearchResult = bookFirstPageDeferred.await()

                bookSearchResult.onSuccess {
                    _bookData.value = it
                }

                if (bookSearchResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }
                if (bookSearchResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Произошла непредвиденная ошибка"
                }

            }

            _loading.value = false
        }
    }

    fun getNextPageBooks() {
        if ((_loading.value ?: false)) {
            return
        }

        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val bookFirstPageDeferred = async { runCatching { repository.getBooksNextPage() } }

                val bookSearchResult = bookFirstPageDeferred.await()

                bookSearchResult.onSuccess {
                    _bookData.value = (_bookData.value ?: mapOf()) + it
                }

                if (bookSearchResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }
                if (bookSearchResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Произошла непредвиденная ошибка"
                }

            }

            _loading.value = false
        }
    }

    fun onErrorShow() {
        _error.value = null
    }

}