package com.example.bookshopapp.data.repository

import com.example.bookshopapp.data.IDontKnowWhatsHappenedException
import com.example.bookshopapp.data.InternetException
import com.example.bookshopapp.data.remote.api.ShopApiService
import com.example.bookshopapp.data.remote.dto.ShopDTO
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okio.IOException
import kotlin.coroutines.cancellation.CancellationException


interface ShopRepository {
    suspend fun getShops(): Map<Int, ShopDTO>
}


class NetworkShopsRepository(
    private val _shopApiService: ShopApiService
) : ShopRepository {
    private var _cachedShops: Map<Int, ShopDTO> = mapOf()
    private val _mutex = Mutex()

    override suspend fun getShops(): Map<Int, ShopDTO> = _mutex.withLock {
        val cache = _cachedShops

        if (cache.isNotEmpty()) {
            cache
        } else {

            try {
                val responseResult = _shopApiService.getShops()
                _cachedShops = responseResult
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
}