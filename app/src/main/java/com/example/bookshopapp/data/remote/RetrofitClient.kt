package com.example.bookshopapp.data.remote

import com.example.bookshopapp.data.remote.api.AuthorApiService
import com.example.bookshopapp.data.remote.api.BookApiService
import com.example.bookshopapp.data.remote.api.CommentApiService
import com.example.bookshopapp.data.remote.api.GenreApiService
import com.example.bookshopapp.data.remote.api.OrderApiService
import com.example.bookshopapp.data.remote.api.ShopApiService
import com.example.bookshopapp.data.remote.api.UserApiService


import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.JavaNetCookieJar
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }


    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }
    private val cookieJar = JavaNetCookieJar(cookieManager)

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()


    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val authorApi: AuthorApiService by lazy { retrofit.create(AuthorApiService::class.java) }
    val bookApi: BookApiService by lazy { retrofit.create(BookApiService::class.java) }
    val commentApi: CommentApiService by lazy { retrofit.create(CommentApiService::class.java) }
    val genreApi: GenreApiService by lazy { retrofit.create(GenreApiService::class.java) }
    val shopApi: ShopApiService by lazy { retrofit.create(ShopApiService::class.java) }
    val orderApi: OrderApiService by lazy { retrofit.create(OrderApiService::class.java) }
}