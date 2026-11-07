package com.example.bookshopapp.ui.CartUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookshopapp.R
import com.example.bookshopapp.data.remote.dto.BookDTO
import kotlin.math.round


class CartItemsDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem.bookId == newItem.bookId

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean =
        oldItem == newItem
}

class CartItemRecycleViewAdapter(
    private val onClearClick: (itemId: Int) -> Unit,
) : ListAdapter<CartItem, CartItemRecycleViewAdapter.ViewHolder>(CartItemsDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCartItemTitle: TextView = view.findViewById(R.id.tvCartItemTitle)
        val tvCartItemCount: TextView = view.findViewById(R.id.tvCartItemCount)
        val tvCartItemPrice: TextView = view.findViewById(R.id.tvCartItemPrice)
        val btnRemoveItem: ImageButton = view.findViewById(R.id.btnRemoveItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBook = getItem(position)
        holder.tvCartItemTitle.text = currentBook.bookTitle

        val countText = "${currentBook.bookCount} шт."
        holder.tvCartItemCount.text = countText

        val priceText = "${"%.2f".format(currentBook.bookCount * currentBook.bookPrice)} руб."
        holder.tvCartItemPrice.text = priceText


        holder.btnRemoveItem.setOnClickListener {
            onClearClick(currentBook.bookId)
        }
    }

}