package com.example.bookshopapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookshopapp.databinding.CatalogActivityBinding
import com.example.bookshopapp.databinding.ProfileActivityBinding

class ProfileActivity : AppCompatActivity() {
    private var _binding: ProfileActivityBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_Binding in activity doesn't initialaze")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}