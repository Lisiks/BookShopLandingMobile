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

    val title: String?
    val pageIsLast: Boolean

    suspend fun getBooks(curTitle: String? = null): Map<Int, BookDTO>
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

    override val pageIsLast: Boolean
        get() = _pageIsLast

    private var _title: String? = null

    override val title: String?
        get() = _title

    override suspend fun getBooks(
        curTitle: String?,
    ): Map<Int, BookDTO> = _mutex.withLock {

        _title = curTitle
        _page = 0
        _pageIsLast = false

        try {
            val responseResult = _bookApiService.getBooks(_page, _title)

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
        if (pageIsLast) {
            mapOf()
        } else {

            try {
                val responseResult = _bookApiService.getBooks(_page + 1, _title)
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