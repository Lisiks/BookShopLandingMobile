package com.example.bookshopapp.ui.BookUI

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.ConflictException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.dto.BookDTO
import com.example.bookshopapp.data.remote.dto.CommentDTO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

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

            supervisorScope {
                val bookDataDeferred = async { runCatching { bookRepository.getBookById(bookId) } }
                val commentDataDeferred = async { runCatching { commentRepository.getComments(bookId) } }
                val myCommentDataDeferred = async { runCatching { commentRepository.getCommentMe(bookId) } }

                val bookDataResult = bookDataDeferred.await()
                val commentDataResult = commentDataDeferred.await()
                val myCommentDataResult = myCommentDataDeferred.await()

                bookDataResult.onSuccess {
                    _bookData.value = it
                }

                if (commentDataResult.isSuccess && myCommentDataResult.isSuccess) {
                    val bookComments = commentDataResult.getOrNull() ?: listOf()
                    val myCommentData = myCommentDataResult.getOrNull()


                    _commentData.value = bookComments.filter { commentData ->
                        commentData.userId != myCommentData?.userId }
                    _myCommentText.value = myCommentData?.text
                }

                if (bookDataResult.exceptionOrNull() is InternetException ||
                    commentDataResult.exceptionOrNull() is InternetException ||
                    myCommentDataResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (bookDataResult.exceptionOrNull() is NotFoundException ||
                    commentDataResult.exceptionOrNull() is NotFoundException) {
                    _error.value = "Книга не найдена"
                }

                if (bookDataResult.exceptionOrNull() != null ||
                    commentDataResult.exceptionOrNull() != null ||
                    myCommentDataResult.exceptionOrNull() != null) {
                    _error.value = "Непредвиденная ошибка"
                }

            }
            _bookDataLoading.value = false
        }
    }

    fun createComment(text: String) {

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

            supervisorScope {
                val createCommentDeferred = async { runCatching { commentRepository.createComment(bookId, text) } }
                val createCommentResult = createCommentDeferred.await()

                createCommentResult.onSuccess {
                    _myCommentText.value = text
                }

                if (createCommentResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (createCommentResult.exceptionOrNull() is AuthException) {
                    _error.value = "Чтобы оставилять комментарии необходимо войти в профиль"
                }

                if (createCommentResult.exceptionOrNull() is ConflictException) {
                    _error.value = "Вы уже оставляли комментарий к данной книге"
                }

                if (createCommentResult.exceptionOrNull() is NotFoundException) {
                    _error.value = "Книга, к которой вы оставляете комментарий более недоступна"
                }

                if (createCommentResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }
            _myCommentLoading.value = false

        }
    }

    fun deleteComment() {
        val bookId = _bookData.value?.id ?: 0
        if (bookId == 0) {
            _error.value = "Данные о книге пока не загружены"
            return
        }

        viewModelScope.launch {
            _myCommentLoading.value = true

            supervisorScope {
                val deleteCommentDeferred = async { runCatching { commentRepository.deleteComment(bookId) } }
                val deleteCommentResult = deleteCommentDeferred.await()

                deleteCommentResult.onSuccess {
                    _myCommentText.value = null
                }

                if (deleteCommentResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (deleteCommentResult.exceptionOrNull() is AuthException) {
                    _error.value = "Ваша сессия истекла, осуществите повторный вход в профиль"
                }

                if (deleteCommentResult.exceptionOrNull() is NotFoundException) {
                    _error.value = "Комментарий к данной книге не найден"
                }

                if (deleteCommentResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
                    _error.value = "Непредвиденная ошибка"
                }
            }
            _myCommentLoading.value = false

        }
    }



    fun onErrorShow() {
        _error.value = null
    }

}
