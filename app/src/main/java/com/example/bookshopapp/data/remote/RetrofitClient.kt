package com.example.bookshopapp.data.remote

import android.content.Context
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
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit
import net.gotev.cookiestore.SharedPreferencesCookieStore


class RetrofitClient(
    context: Context
) {
    // Перед стартом на localhost - пробросить порт adb reverse tcp:8000 tcp:8000

    private val baseUrl = "http://127.0.0.1:8000/"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }


    private val cookieManager = CookieManager(
        SharedPreferencesCookieStore(context, "application_cookies"),
        CookiePolicy.ACCEPT_ALL
    )

    private val cookieJar = JavaNetCookieJar(cookieManager)


    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(cookieJar)
        .addInterceptor(logging)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    val authorApi: AuthorApiService by lazy { retrofit.create(AuthorApiService::class.java) }
    val genreApi: GenreApiService by lazy { retrofit.create(GenreApiService::class.java) }
    val userApi: UserApiService by lazy { retrofit.create(UserApiService::class.java) }
    val bookApi: BookApiService by lazy { retrofit.create(BookApiService::class.java) }
    val commentApi: CommentApiService by lazy { retrofit.create(CommentApiService::class.java) }
    val shopApi: ShopApiService by lazy { retrofit.create(ShopApiService::class.java) }
    val orderApi: OrderApiService by lazy { retrofit.create(OrderApiService::class.java) }
}