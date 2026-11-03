package com.example.bookshopapp.data.repository


import com.example.bookshopapp.data.AuthException
import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.NotFoundException
import com.example.bookshopapp.data.remote.api.OrderApiService
import com.example.bookshopapp.data.remote.dto.OrderDTO
import com.example.bookshopapp.data.remote.dto.OrderPostDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import retrofit2.HttpException
import kotlin.coroutines.cancellation.CancellationException

interface OrderRepository {
    suspend fun clearCache()
    suspend fun getCachedOrders(): Map<Int, OrderDTO>
    suspend fun getOrders():  Map<Int, OrderDTO>
    suspend fun getOrdersNextPage(): Map<Int, OrderDTO>
    suspend fun createOrder(shopId: Int, bookCartData: List<Map<String, Int>>)
}


class NetworkOrderRepository(
    private val orderApiService: OrderApiService
) : OrderRepository {
    private var page = 0
    private var pageIsLast = false
    private var cachedOrders: MutableMap<Int, OrderDTO> = mutableMapOf()
    private val mutex = Mutex()

    override suspend fun clearCache() {
        mutex.withLock {
            cachedOrders = mutableMapOf()
        }
    }

    override suspend fun getCachedOrders(): Map<Int, OrderDTO> = mutex.withLock {
        cachedOrders
    }

    override suspend fun getOrders(): Map<Int, OrderDTO> = mutex.withLock {
        page = 0
        pageIsLast = false


        try {
            val responseResult = orderApiService.getOrders(page)

            if (responseResult.isEmpty()) {
                pageIsLast = true
            }

            cachedOrders = responseResult.toMutableMap()
            cachedOrders

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

    override suspend fun getOrdersNextPage(): Map<Int, OrderDTO> = mutex.withLock {
        if (pageIsLast) {
            cachedOrders
        } else {


            try {
                val responseResult = orderApiService.getOrders(page++)

                if (responseResult.isEmpty()) {
                    pageIsLast = true
                } else {
                    page++
                }

                cachedOrders.putAll(responseResult)
                cachedOrders

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
    }

    override suspend fun createOrder(shopId: Int, bookCartData: List<Map<String, Int>>) {
        mutex.withLock {
            if (bookCartData.size !in 1..10) {
                throw IllegalArgumentException("Cart must include at least 1 and not than 10 elements")
            }


            try {
                val responseBody = OrderPostDTO(shopId, bookCartData)
                orderApiService.createOrder(responseBody)

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 403) {
                    throw AuthException("You are unathorize")
                } else if (e.code() == 404) {
                    throw NotFoundException("Some books are not found")
                }
                else {
                    throw IDontKnowWhatsHappenedException("IDK")
                }
            } catch (e: Exception) {
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }


}