package com.example.bookshopapp.ui.CatalogUI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import com.example.bookshopapp.ui.SearchBookUI.SearchBookActivity
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


    private val searchParametersLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val minPrice = result.data?.getDoubleExtra("minPrice", -1.0)
            val maxPrice = result.data?.getDoubleExtra("maxPrice", -1.0)
            val authorId = result.data?.getIntExtra("authorId", -1)
            val genreId = result.data?.getIntExtra("genreId", -1)

            viewModel.setMinPrice(if (minPrice != -1.0) minPrice else null)
            viewModel.setMaxPrice(if (maxPrice != -1.0) maxPrice else null)
            viewModel.setAuthorId(if (authorId != -1) authorId else null)
            viewModel.setGenreId(if (genreId != -1) genreId else null)
            viewModel.getFirstBookPage()
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
                val intent = Intent(this, BookActivity::class.java).apply {
                    putExtra("bookId", bookId)
                }
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
            binding.btnSearchSetting.isEnabled = !isLoading
        }

        viewModel.title.observe(this) { inputTitle ->
            binding.etSearch.setText(inputTitle)
        }

        binding.etSearch.setOnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                viewModel.setTitle(binding.etSearch.text.toString())
                viewModel.getFirstBookPage()
            }
        }

        binding.btnSearchSetting.setOnClickListener {
            val intent = Intent(this, SearchBookActivity::class.java).apply {
                putExtra("minPrice", viewModel.minPrice.value)
                putExtra("maxPrice", viewModel.maxPrice.value)
                putExtra("authorId", viewModel.authorId.value)
                putExtra("genreId", viewModel.genreId.value)
            }
            searchParametersLauncher.launch(intent)
        }


        binding.btnClear.setOnClickListener {
            binding.etSearch.setText("")

            viewModel.setTitle(null)
            viewModel.setAuthorId(null)
            viewModel.setGenreId(null)
            viewModel.setMinPrice(null)
            viewModel.setMaxPrice(null)
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

    override fun onResume() {
        super.onResume()
        viewModel.searchOnResume()
    }

    override fun onDestroy() {
        _binding?.llBooks?.clearOnScrollListeners()
        _binding = null
        super.onDestroy()
    }

}