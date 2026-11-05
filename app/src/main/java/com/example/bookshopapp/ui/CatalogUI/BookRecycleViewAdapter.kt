package com.example.bookshopapp.ui.CatalogUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookshopapp.R
import com.example.bookshopapp.data.remote.dto.BookDTO
import org.w3c.dom.Text
import kotlin.math.round

class BookRecycleViewAdapter(
    private val bookList: List<BookDTO>
) : RecyclerView.Adapter<BookRecycleViewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvBookTitle)
        val tvAuthor: TextView = view.findViewById(R.id.tvBookAuthor)
        val tvGenre: TextView = view.findViewById(R.id.tvBookGenre)
        val tvPrice: TextView = view.findViewById(R.id.tvBookPrice)
        val ivImage: ImageView = view.findViewById(R.id.ivBookPhoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvTitle.text = bookList[position].title

        val authorString = "${bookList[position].author.f} ${bookList[position].author.i[0]}. ${bookList[position].author.o[0]}."
        holder.tvAuthor.text = authorString
        holder.tvGenre.text = bookList[position].genre.name

        val priceString = "${round(bookList[position].price * 100) / 100} руб."
        holder.tvPrice.text = priceString
        holder.ivImage.load("http://127.0.0.1:8000/${bookList[position].imageFilePath}")
    }

    override fun getItemCount() = bookList.size
}