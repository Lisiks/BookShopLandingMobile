package com.example.bookshopapp.ui.CatalogUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookshopapp.R
import com.example.bookshopapp.data.remote.dto.BookDTO
import org.w3c.dom.Text
import kotlin.math.round


class BookDiffCallback : DiffUtil.ItemCallback<BookDTO>() {
    override fun areItemsTheSame(oldItem: BookDTO, newItem: BookDTO): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: BookDTO, newItem: BookDTO): Boolean =
        oldItem == newItem
}

class BookRecycleViewAdapter(
    private val onItemClick: (itemId: Int) -> Unit,
    private val openAddToCartWindow: (bookId: Int, bookTitle: String, bookPrice: Double) -> Unit,
) : ListAdapter<BookDTO, BookRecycleViewAdapter.ViewHolder>(BookDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvBookAuthor)
        val tvGenre: TextView = view.findViewById(R.id.tvBookGenre)
        val tvPrice: TextView = view.findViewById(R.id.tvBookPrice)
        val ivImage: ImageView = view.findViewById(R.id.ivBookPhoto)

        val clItem: androidx.constraintlayout.widget.ConstraintLayout = view.findViewById(R.id.clOrderItem)
        val addToCartBtn: ImageButton = view.findViewById(R.id.btnToCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentBook = getItem(position)
        holder.tvTitle.text = currentBook.title

        val authorString = "${currentBook.author.f} ${currentBook.author.i[0]}. ${currentBook.author.o[0]}."
        holder.tvAuthor.text = authorString
        holder.tvGenre.text = currentBook.genre.name

        val priceString = "${round(currentBook.price * 100) / 100} руб."
        holder.tvPrice.text = priceString
        holder.ivImage.load("http://127.0.0.1:8000/${currentBook.imageFilePath}")

        holder.clItem.setOnClickListener {
            onItemClick(currentBook.id)
        }

        holder.addToCartBtn.setOnClickListener {
            openAddToCartWindow(currentBook.id, currentBook.title, currentBook.price)
        }
    }

}