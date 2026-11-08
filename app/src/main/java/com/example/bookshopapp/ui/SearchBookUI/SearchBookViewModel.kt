package com.example.bookshopapp.ui.SearchBookUI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

class SearchBookViewModel (application: Application): AndroidViewModel(application) {
    private val authorRepository = (application as BookShopApplication).authorsRepository
    private val genreRepository = (application as BookShopApplication).genresRepository

    private val _authorsData = MutableLiveData<Map<String, Int>>(mapOf())
    private val _genresData = MutableLiveData<Map<String, Int>>(mapOf())


    private val _error = MutableLiveData<String?>(null)
    private val _loading = MutableLiveData(false)

    val error: LiveData<String?> = _error
    val loading: LiveData<Boolean> = _loading

    val authorsData: LiveData<Map<String, Int>> = _authorsData
    val genresData: LiveData<Map<String, Int>> = _genresData



    fun loadAuthorGenresData() {
        viewModelScope.launch {
            _loading.value = true
            supervisorScope {
                val authorDataDeferred = async { runCatching { authorRepository.getAuthors() } }
                val genresDataDeferred = async { runCatching { genreRepository.getGenres() } }

                val authorDataResult = authorDataDeferred.await()
                val genresDataResult = genresDataDeferred.await()

                authorDataResult.onSuccess {
                    _authorsData.value = mapOf("Выберите автора" to -1) + it.values.map { authorData ->
                        "${authorData.f} ${authorData.i} ${authorData.o}" to authorData.id
                    }.toMap()
                }

                genresDataResult.onSuccess {
                    _genresData.value = mapOf("Выберите жанр" to -1) + it.values.map { genreData ->
                        genreData.name to genreData.id
                    }.toMap()
                }

                if (authorDataResult.exceptionOrNull() is InternetException || genresDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (authorDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException|| genresDataResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }
            _loading.value = false
        }
    }

    fun onErrorShow() {
        _error.value = null
    }
}