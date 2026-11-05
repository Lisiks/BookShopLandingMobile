package com.example.bookshopapp.ui.registerUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.RegisterActivityBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.LoginViewModel
import com.example.bookshopapp.ui.login.RegisterViewModel
import com.example.bookshopapp.ui.loginUI.LoginActivity
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {
    private var _binding: RegisterActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString(),
                binding.etRepeatPassword.text.toString()
            )
        }

        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.loading.observe(this) { loading ->
            binding.btnRegister.isEnabled = !loading
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.registerSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
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