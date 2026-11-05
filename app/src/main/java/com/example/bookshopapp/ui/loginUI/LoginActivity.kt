package com.example.bookshopapp.ui.loginUI

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.example.bookshopapp.databinding.LoginActivityBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.LoginViewModel
import com.example.bookshopapp.ui.registerUI.RegisterActivity
import com.google.android.material.snackbar.Snackbar


class LoginActivity : AppCompatActivity() {
    private var _binding: LoginActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }

        viewModel.loading.observe(this) { loading ->
            binding.btnLogin.isEnabled = !loading
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.loginSuccess.observe(this) { success ->
            if (success) {
                startActivity(Intent(this, ProfileActivity::class.java))
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