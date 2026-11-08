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

    suspend fun getOrders():  Map<Int, OrderDTO>
    suspend fun getOrdersNextPage(): Map<Int, OrderDTO>
    suspend fun createOrder(shopId: Int, bookCartData: List<Map<String, Int>>)
}


class NetworkOrderRepository(
    private val _orderApiService: OrderApiService
) : OrderRepository {
    private var _page = 0
    private var _pageIsLast = false

    private val _mutex = Mutex()


    override suspend fun getOrders(): Map<Int, OrderDTO> = _mutex.withLock {
        _page = 0
        _pageIsLast = false


        try {
            val responseResult = _orderApiService.getOrders(_page)

            if (responseResult.isEmpty()) {
                _pageIsLast = true
            }

             responseResult

        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            throw InternetException("Bad internet")
        } catch (e: HttpException) {
            if (e.code() == 401) {
                throw AuthException("You are unathorize")
            } else {
                throw IDontKnowWhatsHappenedException("IDK")
            }
        } catch (e: Exception) {
            throw IDontKnowWhatsHappenedException("IDK")
        }

    }

    override suspend fun getOrdersNextPage(): Map<Int, OrderDTO> = _mutex.withLock {
        if (_pageIsLast) {
            mapOf()
        } else {
            try {
                val responseResult = _orderApiService.getOrders(_page + 1)

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
            } catch (e: HttpException) {
                if (e.code() == 401) {
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
        _mutex.withLock {
            if (bookCartData.size !in 1..10) {
                throw IllegalArgumentException("Cart must include at least 1 and not than 10 elements")
            }

            try {
                val responseBody = OrderPostDTO(shopId, bookCartData)
                _orderApiService.createOrder(responseBody)

            } catch (e: CancellationException) {
                throw e
            } catch (e: IOException) {
                throw InternetException("Bad internet")
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    throw AuthException("You are unathorize")
                } else if (e.code() == 404) {
                    throw NotFoundException("Some books are not found")
                }
                else {
                    throw IDontKnowWhatsHappenedException("IDK")
                }
            } catch (e: Exception) {
                println(e.message)
                throw IDontKnowWhatsHappenedException("IDK")
            }

        }
    }
}