package com.example.bookshopapp.data.repository



import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.api.BookApiService
import com.example.bookshopapp.data.remote.dto.BookDTO
import com.example.bookshopapp.data.remote.dto.GetBookListDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException


interface BookRepository {


    suspend fun getBooks(
        curTitle: String? = null,
        curAuthorId: Int? = null,
        curGenreId: Int? = null,
        curMinPrice: Double? = null,
        curMaxPrice: Double? = null
    ): Map<Int, BookDTO>
    suspend fun getBooksNextPage(): Map<Int, BookDTO>
    suspend fun getBookById(bookId: Int): BookDTO
    suspend fun getBookByList(bookIdsList: List<Int>):  Map<Int, BookDTO>
}


class NetworkBookRepository(
    private val _bookApiService: BookApiService
) : BookRepository {
    private val _mutex = Mutex()

    private var _page: Int = 0
    private var _pageIsLast = false



    private var _title: String? = null
    private var _authorId: Int? = null
    private var _genreId: Int? = null
    private var _minPrice: Double? = null
    private var _maxPrice: Double? = null


    override suspend fun getBooks(
        curTitle: String?,
        curAuthorId: Int?,
        curGenreId: Int?,
        curMinPrice: Double?,
        curMaxPrice: Double?
    ): Map<Int, BookDTO> = _mutex.withLock {

        _title = curTitle
        _authorId = curAuthorId
        _genreId = curGenreId
        _minPrice = curMinPrice
        _maxPrice = curMaxPrice

        _page = 0
        _pageIsLast = false

        try {
            val responseResult = _bookApiService.getBooks(_page, _title, _minPrice, _maxPrice, _authorId, _genreId)

            if (responseResult.isEmpty()) {
                _pageIsLast = true
            }
            responseResult

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }

    }

    override suspend fun getBooksNextPage(): Map<Int, BookDTO> = _mutex.withLock {
        if (_pageIsLast) {
            mapOf()
        } else {

            try {
                val responseResult = _bookApiService.getBooks(_page + 1, _title, _minPrice, _maxPrice, _authorId, _genreId)
                if (responseResult.isEmpty()) {
                    _pageIsLast = true
                } else {
                    _page++
                }

                responseResult
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }

    override suspend fun getBookById(bookId: Int): BookDTO = _mutex.withLock{
        try {
            _bookApiService.getBookById(bookId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            if (e.code() == 404) {
                throw NotFoundException("Book doesnt found")
            } else {
                throw IDontKnowWhatsHappenedException("IDK")
            }
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }
    }

    override suspend fun getBookByList(bookIdsList: List<Int>): Map<Int, BookDTO> = _mutex.withLock {
        val responseBody = GetBookListDTO(bookIdsList)
        try {
            val responseResult = _bookApiService.getBooksByList(responseBody)
            responseResult
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }

    }
}