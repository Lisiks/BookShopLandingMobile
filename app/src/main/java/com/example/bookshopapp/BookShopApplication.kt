package com.example.bookshopapp


import android.app.Application
import com.example.bookshopapp.data.remote.RetrofitClient
import com.example.bookshopapp.data.repository.AuthorRepository
import com.example.bookshopapp.data.repository.BookRepository
import com.example.bookshopapp.data.repository.CommentRepository
import com.example.bookshopapp.data.repository.GenreRepository
import com.example.bookshopapp.data.repository.NetworkAuthorRepository
import com.example.bookshopapp.data.repository.NetworkBookRepository
import com.example.bookshopapp.data.repository.NetworkCommentRepository
import com.example.bookshopapp.data.repository.NetworkGenreRepository
import com.example.bookshopapp.data.repository.NetworkOrderRepository
import com.example.bookshopapp.data.repository.NetworkShopsRepository
import com.example.bookshopapp.data.repository.NetworkUserRepository
import com.example.bookshopapp.data.repository.OrderRepository
import com.example.bookshopapp.data.repository.ShopRepository
import com.example.bookshopapp.data.repository.UserRepository

class BookShopApplication : Application() {

    val authorsRepository: AuthorRepository by lazy {
        NetworkAuthorRepository(RetrofitClient.authorApi)
    }

    val booksRepository: BookRepository by lazy {
        NetworkBookRepository(RetrofitClient.bookApi)
    }

    val commentsRepository: CommentRepository by lazy {
        NetworkCommentRepository(RetrofitClient.commentApi)
    }

    val genresRepository: GenreRepository by lazy {
        NetworkGenreRepository(RetrofitClient.genreApi)
    }

    val usersRepository: UserRepository by lazy {
        NetworkUserRepository(RetrofitClient.userApi)
    }

    val shopRepository: ShopRepository by lazy {
        NetworkShopsRepository(RetrofitClient.shopApi)
    }

    val ordersRepository: OrderRepository by lazy {
        NetworkOrderRepository(RetrofitClient.orderApi)
    }

    companion object {
        lateinit var instance: BookShopApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}