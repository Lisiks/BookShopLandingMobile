package com.example.bookshopapp.ui.ProfileFragmentUI


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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


    class OrderRecycleScrollEventListener(
        private val layoutManager: LinearLayoutManager,
        private val scrollFunc: () -> Unit,
        private val itemsVisibleToLoad: Int = 1
    ) : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

            super.onScrolled(recyclerView, dx, dy)

            val totalItems = layoutManager.itemCount
            val lastVisible = layoutManager.findLastVisibleItemPosition()


            if (totalItems - lastVisible <= itemsVisibleToLoad) {
                scrollFunc()
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ordersRecycleViewLayoutManager = LinearLayoutManager(requireContext())
        val orderAdapter = OrderRecycleViewAdapter()
        binding.rwOrders.layoutManager = ordersRecycleViewLayoutManager
        binding.rwOrders.adapter = orderAdapter

        viewModel.orderData.observe(viewLifecycleOwner) { orderData ->
            orderAdapter.submitList(orderData.values.toList())
        }

        val orderScrollEventListener = OrderRecycleScrollEventListener(
            ordersRecycleViewLayoutManager,
            viewModel::loadOrderNextPage,
            3
        )
        binding.rwOrders.addOnScrollListener(orderScrollEventListener)








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

        viewModel.profileLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.btnExit.isEnabled = !isLoading
        }

        binding.btnExit.setOnClickListener {
            viewModel.logout()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadUserData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}