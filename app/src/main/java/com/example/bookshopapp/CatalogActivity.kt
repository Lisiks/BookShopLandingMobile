package com.example.bookshopapp

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshopapp.databinding.CatalogActivityBinding

class CatalogActivity : AppCompatActivity() {
    private var _binding: CatalogActivityBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("_Binding in activity doesn't initialaze")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CatalogActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.icSearch.setOnClickListener {
            val searchActivityIntent = Intent(this, CatalogActivity::class.java)
            startActivity(searchActivityIntent)
        }

        binding.icProfile.setOnClickListener {
            val profileActivityIntent = Intent(this, ProfileActivity::class.java)
            startActivity(profileActivityIntent)
        }


    }
}