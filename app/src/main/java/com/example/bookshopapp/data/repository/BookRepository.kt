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

    val pageIsLastValue: Boolean

    suspend fun getCachedBooks(): Map<Int, BookDTO>
    suspend fun getBooks(
        curTitle: String? = null,
        curMaxPrice: Double? = null,
        curMinPrice: Double? = null,
        curGenreId: Int? = null,
        curAuthorId: Int? = null
    ): Map<Int, BookDTO>
    suspend fun getBooksNextPage(): Map<Int, BookDTO>
    suspend fun getBookById(bookId: Int): BookDTO
    suspend fun getBookByList(bookIdsList: List<Int>):  Map<Int, BookDTO>
}


class NetworkBookRepository(
    private val bookApiService: BookApiService
) : BookRepository {
    private var cachedBooks: MutableMap<Int, BookDTO> = mutableMapOf()
    private val mutex = Mutex()

    private var page: Int = 0
    private var pageIsLast = false

    override val pageIsLastValue
        get() = pageIsLast


    var title: String? = null
        private set
    var maxPrice: Double? = null
        private set

   var minPrice: Double? = null
        private set

    var genreId: Int? = null
        private set

    var authorId: Int? = null
        private set


    override suspend fun getCachedBooks(): Map<Int, BookDTO> = mutex.withLock {
        return cachedBooks
    }

    override suspend fun getBooks(
        curTitle: String?,
        curMaxPrice: Double?,
        curMinPrice: Double?,
        curGenreId: Int?,
        curAuthorId: Int?
    ): Map<Int, BookDTO> = mutex.withLock {

        if (curMaxPrice != null && (curMaxPrice !in 0.0..99999999.99)) {
            throw IllegalArgumentException("MaxPrice must be in range 0.0..99999999.99")
        }

        if (curMinPrice != null && (curMinPrice !in 0.0..99999999.99)) {
            throw IllegalArgumentException("MinPrice must be in range 0.0..99999999.99")
        }

        title = curTitle
        maxPrice = curMaxPrice
        minPrice = curMinPrice
        genreId = curGenreId
        authorId = curAuthorId

        cachedBooks = mutableMapOf()
        page = 0
        pageIsLast = false

        try {
            val responseResult = bookApiService.getBooks(page, title, minPrice, maxPrice, authorId, genreId)

            if (responseResult.isEmpty()) {
                pageIsLast = true
            }
            cachedBooks = responseResult.toMutableMap()
            cachedBooks

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }

    }

    override suspend fun getBooksNextPage(): Map<Int, BookDTO> = mutex.withLock {
        if (pageIsLast) {
            cachedBooks
        } else {

            try {
                val responseResult = bookApiService.getBooks(page + 1, title, minPrice, maxPrice, authorId, genreId)
                if (responseResult.isEmpty()) {
                    pageIsLast = true
                } else {
                    page++
                }

                cachedBooks.putAll(responseResult)
                cachedBooks
            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }

    override suspend fun getBookById(bookId: Int): BookDTO = mutex.withLock{
        try {
            bookApiService.getBookById(bookId)
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

    override suspend fun getBookByList(bookIdsList: List<Int>): Map<Int, BookDTO> = mutex.withLock {
        val responseBody = GetBookListDTO(items = bookIdsList)
        try {
            val responseResult = bookApiService.getBooksByList(responseBody)
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