package com.example.bookshopapp.data.repository

import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.ConflictException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.LoginException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.api.UserApiService
import com.example.bookshopapp.data.remote.dto.UserDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

interface UserRepository {
    suspend fun getUserInfo(): UserDTO
    suspend fun logout()
    suspend fun login(username: String, plainPassword: String)
    suspend fun register(username: String, plainPassword: String, confirmPassword: String)
}

class NetworkUserRepository(
    private val userApiService: UserApiService
) : UserRepository {
    val mutex = Mutex()

    override suspend fun getUserInfo(): UserDTO = mutex.withLock {
        try {
            val responseResult = userApiService.getUserInfo()
            responseResult
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: HttpException) {
            if (e.code() == 403) {
                throw AuthException("You are unathorize")
            } else {
                throw IDontKnowWhatsHappenedException("IDK")
            }
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }

    override suspend fun logout() {
        mutex.withLock {

            try {
                userApiService.logout()
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }

    override suspend fun login(username: String, plainPassword: String) {
        mutex.withLock {
            if (username.length < 6) {
                throw IllegalArgumentException("Username length must be >= 6")
            }

            if (plainPassword.length < 6) {
                throw IllegalArgumentException("Password length must be >= 6")
            }


            try {
                userApiService.login(username, plainPassword)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 400) {
                    throw LoginException("You are already authorized")
                } else if (e.code() == 401) {
                    throw LoginException("Incorrect user or password")
                } else {
                    throw IDontKnowWhatsHappenedException("IDK")
                }
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }

    override suspend fun register(
        username: String,
        plainPassword: String,
        confirmPassword: String
    ) {
        mutex.withLock {
            if (username.length < 6) {
                throw IllegalArgumentException("Username length must be >= 6")
            }

            if (plainPassword.length < 6) {
                throw IllegalArgumentException("Password length must be >= 6")
            }

            if (plainPassword != confirmPassword) {
                throw IllegalArgumentException("Plain password and confirm password are different")
            }


            try {
                userApiService.register(username, plainPassword)
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 409) {
                    throw ConflictException("User with thid username already exists")
                }
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }
}