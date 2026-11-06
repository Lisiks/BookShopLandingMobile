package com.example.bookshopapp.ui.ProfileUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshopapp.databinding.ProfileActivityBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.google.android.material.snackbar.Snackbar

class ProfileActivity : AppCompatActivity() {
    private var _binding: ProfileActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


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
    }
}