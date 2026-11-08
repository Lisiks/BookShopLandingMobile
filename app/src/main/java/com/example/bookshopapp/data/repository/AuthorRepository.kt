package com.example.bookshopapp.data.repository

import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.remote.api.AuthorApiService
import com.example.bookshopapp.data.remote.dto.AuthorDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException


interface AuthorsRepository {
    suspend fun getAuthors(): Map<Int, AuthorDTO>
}


class NetworkAuthorRepository(
    private val authorApiService: AuthorApiService
) : AuthorsRepository {

    private val mutex = Mutex()

    override suspend fun getAuthors(): Map<Int, AuthorDTO> = mutex.withLock {
        try {
            authorApiService.getAuthors()
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }
}