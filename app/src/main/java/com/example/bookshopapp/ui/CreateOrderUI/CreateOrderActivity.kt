package com.example.bookshopapp.ui.CreateOrderUI

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.ListAdapter
import com.example.bookshopapp.databinding.CreateOrderActivityBinding
import com.example.bookshopapp.ui.CartUI.CartViewModel
import com.google.android.material.snackbar.Snackbar

class CreateOrderActivity : AppCompatActivity() {
    private var _binding: CreateOrderActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: CreateOrderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = CreateOrderActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shopsListEtAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mutableListOf())
        binding.actvShop.setAdapter(shopsListEtAdapter)


        binding.actvShop.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedShop = shopsListEtAdapter.getItem(position) ?: ""
                viewModel.setSelectedShop(selectedShop)

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // вызывается, когда выбор сброшен (редко)
            }
        }


        viewModel.loadCartData()

        viewModel.bookCartDataMap.observe(this) {
            val fullPriceString = "Итого: ${"%.2f".format(viewModel.calculateCartPrice())} руб."
            binding.tvOrderSum.text = fullPriceString
        }

        viewModel.shopsDataMap.observe(this) {shopData ->
            if (!shopsListEtAdapter.isEmpty) {
                shopsListEtAdapter.clear()
            }
            shopsListEtAdapter.addAll(shopData.keys)

        }

        viewModel.loading.observe(this) { isLoading ->
            binding.btnCreateOrder.isEnabled = !isLoading
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

        viewModel.orderCreated.observe(this) { isCreated ->
            if (isCreated) {
                finish()
            }
        }

        binding.btnCreateOrder.setOnClickListener {
            viewModel.createOrder()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}