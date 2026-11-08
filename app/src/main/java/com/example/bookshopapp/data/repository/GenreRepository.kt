package com.example.bookshopapp.data.repository

import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.remote.api.GenreApiService
import com.example.bookshopapp.data.remote.dto.GenreDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException


interface GenreRepository {
    suspend fun getGenres(): Map<Int, GenreDTO>
}


class NetworkGenreRepository(
    private val genreApiService: GenreApiService
) : GenreRepository {
    private val mutex = Mutex()

    override suspend fun getGenres(): Map<Int, GenreDTO> = mutex.withLock {
        try {
            genreApiService.getGenres()
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }
}