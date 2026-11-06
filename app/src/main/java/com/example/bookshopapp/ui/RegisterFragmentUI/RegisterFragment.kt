package com.example.bookshopapp.ui.RegisterFragmentUI

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.RegisterFragmentBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.LoginFragmentUI.LoginFragment
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.RegisterViewModel
import com.google.android.material.snackbar.Snackbar

class RegisterFragment : Fragment() {
    private var _binding: RegisterFragmentBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = RegisterFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnRegister.setOnClickListener {
            viewModel.register(
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString(),
                binding.etRepeatPassword.text.toString()
            )
        }

        binding.tvLoginLink.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment_host, LoginFragment())
                .commit()
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.btnRegister.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.registerSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_host, LoginFragment())
                    .commit()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}