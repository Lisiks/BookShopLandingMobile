package com.example.bookshopapp.ui.login


import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookshopapp.BookShopApplication
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.LoginException


import kotlinx.coroutines.launch


class LoginViewModel (application: android.app.Application): AndroidViewModel(application) {
    private val repository = (application as BookShopApplication).usersRepository

    private val _loading = MutableLiveData(false)
    private val _loginSuccess = MutableLiveData(false)
    private val _error = MutableLiveData<String?>(null)


    val loading: LiveData<Boolean> = _loading
    val error: LiveData<String?> = _error
    val loginSuccess: LiveData<Boolean> = _loginSuccess


    fun login(login: String, password: String) {

        viewModelScope.launch {
            _loading.value = true
            try {
                repository.login(login, password)
                _loginSuccess.value = true

            } catch (e: IllegalArgumentException) {
                if (e.message == "Username length must be >= 6") {
                    _error.value =
                        "Слишком короткое имя пользователя. Оно должно составлять не менее 6 символов"
                } else {
                    _error.value =
                        "Слишком короткий пароль. Он должен составлять не менее 6 символов"
                }
            } catch (e: InternetException) {
                _error.value = "Ошибка подключения к сети. Проверьте соединение с интернетом"
            } catch (e: LoginException) {

                if (e.message == "You are already authorized") {
                    _loginSuccess.value = true
                } else {
                    _error.value = "Некорректный логин или пароль"
                }

            } finally {
                _loading.value = false
            }
        }
    }

    fun onErrorShow() {
        _error.value = null
    }

}