package com.example.bookshopapp.ui.CartUI

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bookshopapp.R
import com.example.bookshopapp.databinding.AddToCartActivityBinding
import com.example.bookshopapp.databinding.CartActivityBinding
import com.example.bookshopapp.ui.CatalogUI.CatalogActivity
import com.example.bookshopapp.ui.ProfileUI.ProfileActivity
import com.example.bookshopapp.ui.login.CatalogViewModel
import com.google.android.material.snackbar.Snackbar

class CartActivity : AppCompatActivity() {
    private var _binding: CartActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: CartViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CartActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cartItemRecycleViewLayoutManager = LinearLayoutManager(this)

        val cartItemRecycleViewAdapter = CartItemRecycleViewAdapter(
            {itemId -> viewModel.deleteCartItem(itemId)}
        )
        binding.rwItems.layoutManager = cartItemRecycleViewLayoutManager
        binding.rwItems.adapter = cartItemRecycleViewAdapter

        viewModel.bookCartDataMap.observe(this) { cartItemMap ->
            cartItemRecycleViewAdapter.submitList(cartItemMap.values.toList())

            val orderSumText = "Итого: ${"%.2f".format(viewModel.calculateCartPrice())} руб."
            binding.tvOrderSum.text = orderSumText
        }


        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            binding.btnCreateOrder.isEnabled = !isLoading
            binding.btnClear.isEnabled = !isLoading
        }

        binding.btnClear.setOnClickListener {
            viewModel.clearCartData()
        }

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

        binding.icCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            startActivity(intent)
        }
    }



    override fun onResume() {
        super.onResume()
        viewModel.loadCartData()
    }
}