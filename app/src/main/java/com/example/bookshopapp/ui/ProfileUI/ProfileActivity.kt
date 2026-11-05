package com.example.bookshopapp.ui.ProfileUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.CatalogActivityBinding
import com.example.bookshopapp.databinding.ProfileActivityBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.login.LoginViewModel
import com.example.bookshopapp.ui.loginUI.LoginActivity
import com.google.android.material.snackbar.Snackbar

class ProfileActivity : AppCompatActivity() {
    private var _binding: ProfileActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ProfileActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }


        viewModel.userData.observe(this) { userData ->
            binding.tvUsername.text = userData?.username ?: "Загружается"
        }


        viewModel.userIsAnAthorise.observe(this) { isAnAthorise ->
            if (isAnAthorise) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.logoutLoading.observe(this) { isLoading ->
            binding.btnExit.isEnabled = !isLoading
        }

        binding.btnExit.setOnClickListener {
            viewModel.logout()
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