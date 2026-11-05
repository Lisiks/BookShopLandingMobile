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

    lateinit var retrofitClient: RetrofitClient
        private set

    override fun onCreate() {
        super.onCreate()
        retrofitClient = RetrofitClient(this)
    }


    val authorsRepository: AuthorRepository by lazy {
        NetworkAuthorRepository(retrofitClient.authorApi)
    }

    val booksRepository: BookRepository by lazy {
        NetworkBookRepository(retrofitClient.bookApi)
    }

    val commentsRepository: CommentRepository by lazy {
        NetworkCommentRepository(retrofitClient.commentApi)
    }

    val genresRepository: GenreRepository by lazy {
        NetworkGenreRepository(retrofitClient.genreApi)
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