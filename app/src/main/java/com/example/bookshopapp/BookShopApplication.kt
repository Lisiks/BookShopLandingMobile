package com.example.bookshopapp


import android.app.Application
import com.example.bookshopapp.data.local.CartDataStoreManager
import com.example.bookshopapp.data.remote.RetrofitClient
import com.example.bookshopapp.data.repository.BookRepository
import com.example.bookshopapp.data.repository.CommentRepository
import com.example.bookshopapp.data.repository.NetworkBookRepository
import com.example.bookshopapp.data.repository.NetworkCommentRepository
import com.example.bookshopapp.data.repository.NetworkOrderRepository
import com.example.bookshopapp.data.repository.NetworkShopsRepository
import com.example.bookshopapp.data.repository.NetworkUserRepository
import com.example.bookshopapp.data.repository.OrderRepository
import com.example.bookshopapp.data.repository.ShopRepository
import com.example.bookshopapp.data.repository.UserRepository

class BookShopApplication : Application() {

    lateinit var retrofitClient: RetrofitClient
        private set

    lateinit var cartDataStoreManager: CartDataStoreManager
        private set

    override fun onCreate() {
        super.onCreate()
        retrofitClient = RetrofitClient(this)
        cartDataStoreManager = CartDataStoreManager(this)
    }

    val booksRepository: BookRepository by lazy {
        NetworkBookRepository(retrofitClient.bookApi)
    }

    val commentsRepository: CommentRepository by lazy {
        NetworkCommentRepository(retrofitClient.commentApi)
    }

    val usersRepository: UserRepository by lazy {
        NetworkUserRepository(retrofitClient.userApi)
    }

    val shopRepository: ShopRepository by lazy {
        NetworkShopsRepository(retrofitClient.shopApi)
    }

    val ordersRepository: OrderRepository by lazy {
        NetworkOrderRepository(retrofitClient.orderApi)
    }

}