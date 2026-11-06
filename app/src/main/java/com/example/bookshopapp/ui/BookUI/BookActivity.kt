package com.example.bookshopapp.ui.BookUI

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.BookActivityBinding
import com.example.bookshopapp.databinding.CatalogActivityBinding

class BookActivity : AppCompatActivity() {
    private var _binding: BookActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = BookActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}