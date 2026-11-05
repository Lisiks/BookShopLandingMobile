package com.example.bookshopapp.ui.CatalogUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.CatalogActivityBinding
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.CatalogViewModel
import com.example.bookshopapp.ui.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar

class CatalogActivity : AppCompatActivity() {
    private var _binding: CatalogActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: CatalogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CatalogActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.llBooks.layoutManager = LinearLayoutManager(this)
        viewModel.bookData.observe(this) { bookData ->
            binding.llBooks.adapter = BookRecycleViewAdapter(bookData.values.toList())
        }

        binding.svBooks.setOnScrollChangeListener { v: NestedScrollView, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == (v.getChildAt(0).measuredHeight - v.measuredHeight)) {
                viewModel.getNextPageBooks()
                println("Событие сработало")
            }
        }



        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.bookFullLoaded.observe(this) {isFullLoaded ->
            if (isFullLoaded) {
                println("Событие отменено")
                binding.svBooks.setOnScrollChangeListener(null as NestedScrollView.OnScrollChangeListener?)
            }
        }


        binding.icSearch.setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }

        binding.icProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }
}