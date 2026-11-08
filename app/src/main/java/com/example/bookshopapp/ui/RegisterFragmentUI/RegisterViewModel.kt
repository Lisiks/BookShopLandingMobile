package com.example.bookshopapp.ui.login


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.ConflictException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.LoginException
import kotlinx.coroutines.async


import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope


class RegisterViewModel (application: Application): AndroidViewModel(application) {
    private val repository = (application as BookShopApplication).usersRepository

    private val _loading = MutableLiveData(false)
    private val _registerSuccess = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)


    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error
    val registerSuccess: LiveData<Boolean> = _registerSuccess


    fun register(login: String, password: String, passwordConfirm: String) {

        viewModelScope.launch {
            _loading.value = true

            supervisorScope {
                val registerDeferred = async { runCatching { repository.register(login, password, passwordConfirm)} }
                val registerResult = registerDeferred.await()

                registerResult.onSuccess { _registerSuccess.value = true }

                if (registerResult.exceptionOrNull() is IllegalArgumentException) {
                    if (registerResult.exceptionOrNull()?.message == "Username length must be >= 6") {
                        _error.value =
                            "Слишком короткое имя пользователя. Оно должно составлять не менее 6 символов"

                    } else if (registerResult.exceptionOrNull()?.message == "Password length must be >= 6") {
                        _error.value =
                            "Слишком короткий пароль. Он должен составлять не менее 6 символов"
                    } else {
                        _error.value = "Пароли не совпадают"
                    }
                }

                if (registerResult.exceptionOrNull() is InternetException) {
                    _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
                }

                if (registerResult.exceptionOrNull() is ConflictException) {
                    _error.value = "Пользователь с данный именем уже существует"
                }

                if (registerResult.exceptionOrNull() is IDontKnowWhatsHappenedException) {
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