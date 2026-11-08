package com.example.bookshopapp.ui.SearchBookUI

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.bookshopapp.databinding.SearchActivityBinding
import com.google.android.material.snackbar.Snackbar

class SearchBookActivity : AppCompatActivity() {
    private var _binding: SearchActivityBinding? = null

    private val binding
        get() = _binding ?: throw IllegalStateException("_binding in activity doesn't initilaze")

    private val viewModel: SearchBookViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val minPriceValue = intent.getDoubleExtra("minPrice", -1.0)
        val maxPriceValue = intent.getDoubleExtra("maxPrice", -1.0)

        val authorIdValue = intent.getIntExtra("authorId", -1)
        val genreIdValue = intent.getIntExtra("genreId", -1)

        viewModel.loadAuthorGenresData()


        viewModel.loading.observe(this) { isLoading ->
            binding.btnBack.isEnabled = !isLoading
            binding.btnSumbitSearch.isEnabled = !isLoading
            binding.etGenre.isEnabled = !isLoading
            binding.etAuthor.isEnabled = !isLoading
        }

        binding.etMinPrice.setText(if (minPriceValue != -1.0) minPriceValue.toString() else "")
        binding.etMaxPrice.setText(if (maxPriceValue != -1.0) maxPriceValue.toString() else "")


        viewModel.authorsData.observe(this) { authorData ->
            val authorSpinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, authorData.keys.toMutableList())
            binding.etAuthor.setAdapter(authorSpinnerAdapter)

            val authorString = authorData.entries.firstOrNull { it.value == authorIdValue }?.key
            val authorPosition = authorSpinnerAdapter.getPosition(authorString)
            binding.etAuthor.setSelection(authorPosition)

        }

        viewModel.genresData.observe(this) { genreData ->
            val genreSpinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, genreData.keys.toMutableList())
            binding.etGenre.setAdapter(genreSpinnerAdapter)

            val genreString = genreData.entries.firstOrNull { it.value == genreIdValue }?.key
            val genrePosition = genreSpinnerAdapter.getPosition(genreString)
            binding.etGenre.setSelection(genrePosition)
        }



        binding.btnBack.setOnClickListener {
            val resultIntent = Intent()
            setResult(Activity.RESULT_CANCELED, resultIntent)
            finish()
        }

        binding.btnSumbitSearch.setOnClickListener {
            val currentMinPriceValue = binding.etMinPrice.text.toString().toDoubleOrNull()
            if (currentMinPriceValue != null && ((currentMinPriceValue < 0.01) || (currentMinPriceValue > 99999999.99))) {
                Snackbar.make(binding.root, "Минимальная цена должна быть в промежутке от 0.01 до 99999999.99", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val currentMaxPriceValue = binding.etMaxPrice.text.toString().toDoubleOrNull()
            if (currentMaxPriceValue != null && ((currentMaxPriceValue < 0.01) || (currentMaxPriceValue > 99999999.99))) {
                Snackbar.make(binding.root, "Мaксимальная цена должна быть в промежутке от 0.01 до 99999999.99", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            var currentAuthorId = viewModel.authorsData.value?.get(binding.etAuthor.selectedItem.toString())
            currentAuthorId = if (currentAuthorId != -1) currentAuthorId else null

            var currentGenreId = viewModel.genresData.value?.get(binding.etGenre.selectedItem.toString())
            currentGenreId = if (currentGenreId!= -1) currentGenreId else null

            val resultIntent = Intent()
            resultIntent.putExtra("minPrice", currentMinPriceValue)
            resultIntent.putExtra("maxPrice", currentMaxPriceValue)
            resultIntent.putExtra("authorId", currentAuthorId)
            resultIntent.putExtra("genreId", currentGenreId)

            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }

        viewModel.error.observe(this) { message ->
            if (message != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                viewModel.onErrorShow()
            }
        }

    }
}