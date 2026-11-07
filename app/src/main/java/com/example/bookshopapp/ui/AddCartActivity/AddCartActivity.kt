package com.example.bookshopapp.ui.AddCartActivity


import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshopapp.databinding.AddToCartActivityBinding
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.CatalogViewModel
import com.google.android.material.snackbar.Snackbar


class AddCartActivity : AppCompatActivity() {
    private var _binding: AddToCartActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: AddCartViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = AddToCartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bookId = intent.getIntExtra("bookId", 0)
        val bookTitle = intent.getStringExtra("bookTitle") ?: ""
        val bookPrice = intent.getDoubleExtra("bookPrice", 0.0)

        viewModel.setBookParams(bookId, bookPrice, bookTitle)

        viewModel.bookTitle.observe(this) { currentTitle ->
            binding.tvPageTitle.text = currentTitle
        }

        viewModel.bookCount.observe(this) { currentCount ->
            binding.tvCount.text = currentCount.toString()

            val fullPriceText = "${"%.2f".format(viewModel.calculatePrice())} руб."
            binding.tvPrice.text = fullPriceText
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.btnBack.isEnabled = !isLoading
            binding.btnAddToCart.isEnabled = !isLoading

        }


        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }



        viewModel.bookIsAdded.observe(this) { currentAddedState ->
            if (currentAddedState) {
                finish()
            }
        }

        binding.btnPlusOne.setOnClickListener {
            viewModel.upCount()
        }

        binding.btnMinusOne.setOnClickListener {
            viewModel.downCount()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            viewModel.addBook()
        }



    }
}