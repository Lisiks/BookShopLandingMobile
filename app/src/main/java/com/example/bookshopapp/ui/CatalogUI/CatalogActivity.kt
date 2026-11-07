package com.example.bookshopapp.ui.CatalogUI

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.CartActivityBinding
import com.example.bookshopapp.databinding.CatalogActivityBinding
import com.example.bookshopapp.ui.AddCartActivity.AddCartActivity
import com.example.bookshopapp.ui.BookUI.BookActivity
import com.example.bookshopapp.ui.CartUI.CartActivity
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.CatalogViewModel
import com.example.bookshopapp.ui.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class CatalogActivity : AppCompatActivity() {
    private var _binding: CatalogActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: CatalogViewModel by viewModels()


    class CatalogRecycleScrollEventListener(
        private val layoutManager: LinearLayoutManager,
        private val scrollFunc: () -> Unit,
        private val itemsVisibleToLoad: Int = 1
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            super.onScrolled(recyclerView, dx, dy)

            val totalItems = layoutManager.itemCount
            val lastVisible = layoutManager.findLastVisibleItemPosition()


            if (totalItems - lastVisible <= itemsVisibleToLoad) {
                scrollFunc()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CatalogActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bookRecycleViewLayoutManager = LinearLayoutManager(this)
        binding.llBooks.layoutManager = bookRecycleViewLayoutManager


        val bookAdapter = BookRecycleViewAdapter(
            { bookId ->
                            val intent = Intent(this, BookActivity::class.java)
                            startActivity(intent)
                        },
            { bookId, bookTitle, bookPrice ->
                val intent = Intent(this, AddCartActivity::class.java).apply {
                    putExtra("bookId", bookId)
                    putExtra("bookTitle", bookTitle)
                    putExtra("bookPrice", bookPrice)
                }
                startActivity(intent)
            })



        binding.llBooks.adapter = bookAdapter

        viewModel.bookData.observe(this) { bookData ->
           bookAdapter.submitList(bookData.values.toList())
        }


        val catalogRecycleScrollViewEventListener = CatalogRecycleScrollEventListener(
            bookRecycleViewLayoutManager,
            viewModel::getNextPageBooks,
            3
        )

        binding.llBooks.addOnScrollListener(catalogRecycleScrollViewEventListener)


        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.etSearch.isEnabled = !isLoading
            binding.btnClear.isEnabled = !isLoading
        }

        viewModel.title.observe(this) { inputTitle ->
            binding.etSearch.setText(inputTitle)
        }

        binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.getFirstBookPage(binding.etSearch.text.toString())
            }
        }


        binding.btnClear.setOnClickListener {
            binding.etSearch.setText("")
            viewModel.getFirstBookPage()
        }


        binding.icSearch.setOnClickListener {
            val intent = Intent(this, CatalogActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.icProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }

        binding.icCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        _binding?.llBooks?.clearOnScrollListeners()
        _binding = null
        super.onDestroy()
    }

}