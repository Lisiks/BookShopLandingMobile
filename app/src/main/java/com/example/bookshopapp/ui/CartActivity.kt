package com.example.bookshopapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshopapp.databinding.CartActivityBinding

class CartActivity : AppCompatActivity() {
    private var _binding: CartActivityBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_Binding in activity doesn't initialaze")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigateBtnsListeners()

    }

    private fun setNavigateBtnsListeners() {
        binding.icSearch.setOnClickListener {
            val searchActivityIntent = Intent(this, CatalogActivity::class.java)
            startActivity(searchActivityIntent)
        }

        binding.icProfile.setOnClickListener {
            val profileActivityIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileActivityIntent)
        }

        binding.icCart.setOnClickListener {
            val cartActivityIntent = Intent(this, CartActivity::class.java)
            startActivity(cartActivityIntent)
        }
    }
}