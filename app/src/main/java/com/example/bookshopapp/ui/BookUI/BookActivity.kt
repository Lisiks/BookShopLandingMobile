package com.example.bookshopapp.ui.BookUI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.BookActivityBinding
import com.example.bookshopapp.databinding.CatalogActivityBinding
import com.example.bookshopapp.ui.AddCartActivity.AddCartActivity
import com.example.bookshopapp.ui.AddCartActivity.AddCartViewModel
import com.example.bookshopapp.ui.CartUI.CartActivity
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import androidx.core.net.toUri
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class BookActivity : AppCompatActivity() {
    private var _binding: BookActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = BookActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getIntExtra("bookId", 0)
        viewModel.loadBookData(bookId)


        viewModel.bookData.observe(this) { bookData ->
            if (bookData != null) {
                binding.tvProductTitle.text = bookData.title
                binding.tvDescription.text = bookData.description
                binding.ivBookPhoto.load("http://127.0.0.1:8000/${bookData.imageFilePath}")

                val authorString = "Автор: ${bookData.author.f} ${bookData.author.i} ${bookData.author.o}"
                binding.tvAuthor.text = authorString

                val genreString = "Жарн: ${bookData.genre.name}"
                binding.tvGenre.text = genreString

                val pageCountString = "Кол-во страниц: ${bookData.pageCount.toString()} ст."
                binding.tvBookCount.text = pageCountString

                val writeYearString = "Год написания: ${bookData.writeYear.toString()} г."
                binding.tvWriteYear.text = writeYearString

                val isbnString = "ISBN: ${bookData.isbn}"
                binding.tvISBN.text = isbnString

                val priceString = "${"%.2f".format(bookData.price)} руб."
                binding.tvProductPrice.text = priceString
            }
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.bookDataLoading.observe(this) {isLoading ->
            binding.btnToCart.isEnabled = !isLoading
            binding.btnCreateComment.isEnabled = !isLoading
            binding.btnDeleteComment.isEnabled = !isLoading
        }

        viewModel.myCommentLoading.observe(this) { isLoading ->
            binding.btnCreateComment.isEnabled = !isLoading
            binding.btnDeleteComment.isEnabled = !isLoading
        }

        viewModel.myCommentText.observe(this) { text ->
            if (text != null) {
                binding.myCommentForm.isVisible = true
                binding.myCommentText.text = text
            } else {
                binding.myCommentForm.isVisible = false
            }
        }

        viewModel.commentData.observe(this) { commentList ->
            val commentLinearLayoutInflater = LayoutInflater.from(binding.llCommentList.context)
            binding.llCommentList.removeAllViews()
            commentList.forEach { commentData ->
                val newCommentView = commentLinearLayoutInflater.inflate(R.layout.item_comment, binding.llCommentList, false)

                newCommentView.findViewById<TextView>(R.id.tvUserName).text = commentData.username
                newCommentView.findViewById<TextView>(R.id.tvCommentDate).text = commentData.datetime.split("T")[0]
                newCommentView.findViewById<TextView>(R.id.tvCommentText).text = commentData.text

                binding.llCommentList.addView(newCommentView)
            }
        }


        binding.tvDemoLink.setOnClickListener {
            val bookData = viewModel.bookData.value
            if (bookData != null) {
                val url = "http://127.0.0.1:8000/${bookData.demoFilePath}"
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
            }
        }

        binding.btnToCart.setOnClickListener {
            val bookData = viewModel.bookData.value
            if (bookData != null) {
                val intent = Intent(this, AddCartActivity::class.java).apply {
                    putExtra("bookId", bookId)
                    putExtra("bookTitle", bookData.title)
                    putExtra("bookPrice", bookData.price)
                }
                startActivity(intent)
            }
        }

        binding.btnCreateComment.setOnClickListener {
            viewModel.createComment(binding.etCommentText.text.toString())
            binding.etCommentText.setText("")
        }

        binding.btnDeleteComment.setOnClickListener {
            viewModel.deleteComment()
        }


        binding.btnGoBack.setOnClickListener {
            finish()
        }

        binding.icSearch.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        binding.icProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }

        binding.icCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
            finish()
        }



    }
}