package com.example.bookshopapp.ui.ProfileFragmentUI


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.bookshopapp.R

import com.example.bookshopapp.databinding.ProfileFragmentBinding
import com.example.bookshopapp.databinding.RegisterFragmentBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.LoginFragmentUI.LoginFragment
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {
    private var _binding: ProfileFragmentBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: ProfileViewModel by viewModels()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.error.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }


        viewModel.userData.observe(viewLifecycleOwner) { userData ->
            binding.tvUsername.text = userData?.username ?: "Загружается"
        }


        viewModel.userIsAnAthorise.observe(viewLifecycleOwner) { isAnAthorise ->
            if (isAnAthorise) {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.profile_fragment_host, LoginFragment())
                    .commit()
            }
        }

        viewModel.logoutLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnExit.isEnabled = !isLoading
        }

        binding.btnExit.setOnClickListener {
            viewModel.logout()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}