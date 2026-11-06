package com.example.bookshopapp.ui.LoginFragmentUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.LoginFragmentBinding
import com.example.bookshopapp.ui.ProfileFragmentUI.ProfileFragment
import com.example.bookshopapp.ui.RegisterFragmentUI.RegisterFragment
import com.example.bookshopapp.ui.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar


class LoginFragment : Fragment() {
    private var _binding: LoginFragmentBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: LoginViewModel by viewModels()


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etLogin.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.tvRegisterLink.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.profile_fragment_host, RegisterFragment())
                .commit()
        }

        viewModel.loading.observe(viewLifecycleOwner) { loading ->
            binding.btnLogin.isEnabled = !loading
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.loginSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_host, ProfileFragment())
                    .commit()
            }
        }



    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}