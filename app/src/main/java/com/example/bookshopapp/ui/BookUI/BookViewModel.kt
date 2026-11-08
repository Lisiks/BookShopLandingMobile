package com.example.bookshopapp.ui.BookUI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.ConflictException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.dto.BookDTO
import com.example.bookshopapp.data.remote.dto.CommentDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class BookViewModel (application: Application): AndroidViewModel(application) {
    private val bookRepository = (application as BookShopApplication).booksRepository
    private val commentRepository = (application as BookShopApplication).commentsRepository

    private val _commentData = MutableLiveData<List<CommentDTO>>(listOf())
    private val _bookData = MutableLiveData<BookDTO?>(null)
    private val _myCommentText = MutableLiveData<String?>(null)

    private val _error = MutableLiveData<String?>(null)

    private val _bookDataLoading = MutableLiveData<Boolean>(false)
    private val _myCommentLoading = MutableLiveData<Boolean>(false)

    val commentData: LiveData<List<CommentDTO>> = _commentData
    val bookData: LiveData<BookDTO?> = _bookData
    val myCommentText: LiveData<String?> = _myCommentText
    val bookDataLoading: LiveData<Boolean> = _bookDataLoading
    val myCommentLoading: LiveData<Boolean> = _myCommentLoading
    val error: LiveData<String?> = _error

    fun loadBookData(bookId: Int) {
        viewModelScope.launch {
            _bookDataLoading.value = true

            try {
                val bookDataDeferred = async { bookRepository.getBookById(bookId) }
                val commentDataDeferred = async { commentRepository.getComments(bookId) }
                val myCommentDataDeferred = async { commentRepository.getCommentMe(bookId) }

                _bookData.value = bookDataDeferred.await()
                _commentData.value = commentDataDeferred.await()

                val myCommentData = myCommentDataDeferred.await()
                _myCommentText.value = myCommentData?.text

                if (myCommentData != null) {
                    _commentData.value = _commentData.value?.filter { commentData ->
                        commentData.userId != myCommentData.userId } ?: listOf()
                }

            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: NotFoundException) {
                _error.value = "Книга не найдена"
            } finally {
                _bookDataLoading.value = false
            }
        }
    }

    fun createComment(text: String) {
        println("Калоеееед")

        if (text.trim().length < 3) {
            _error.value = "Длинна комментария должна составлять хотя бы 3 символа"
            return
        }

        val bookId = _bookData.value?.id ?: 0
        if (bookId == 0) {
            _error.value = "Данные о книге пока не загружены"
            return
        }

        viewModelScope.launch {
            _myCommentLoading.value = true
            try {
                commentRepository.createComment(bookId, text)
                _myCommentText.value = text
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: AuthException) {
                _error.value = "Чтобы оставилять комментарии необходимо войти в профиль"
            } catch (e: ConflictException) {
                _error.value = "Вы уже оставляли комментарий к данной книге"
            } catch (e: NotFoundException) {
                _error.value = "Книга, к которой вы оставляете комментарий более недоступна"
            } finally {
                _myCommentLoading.value = false
            }
        }
    }



    fun onErrorShow() {
        _error.value = null
    }

}
