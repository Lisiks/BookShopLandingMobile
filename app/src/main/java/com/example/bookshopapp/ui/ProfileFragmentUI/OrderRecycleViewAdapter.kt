package com.example.bookshopapp.ui.ProfileFragmentUI

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.bookshopapp.R
import com.example.bookshopapp.data.remote.dto.OrderDTO



class OrderDiffCallback : DiffUtil.ItemCallback<OrderDTO>() {
    override fun areItemsTheSame(oldItem: OrderDTO, newItem: OrderDTO): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: OrderDTO, newItem: OrderDTO): Boolean =
        oldItem == newItem
}

class OrderRecycleViewAdapter: ListAdapter<OrderDTO, OrderRecycleViewAdapter.ViewHolder>(OrderDiffCallback()) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvOrderNumber: TextView = view.findViewById(R.id.tvOrderNumber)
        val tvOrderDate: TextView = view.findViewById(R.id.tvOrderDate)
        val tvOrderStatus: TextView = view.findViewById(R.id.tvOrderStatus)
        val tvOrderPrice: TextView = view.findViewById(R.id.tvOrderPrice)

        val tvOrderShop: TextView = view.findViewById(R.id.tvOrderShop)

        val llDescriptionBookList: LinearLayout = view.findViewById(R.id.llDescriptionBookList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOrder = getItem(position)
        val orderNumberString = "Заказ №${currentOrder.id}"

        holder.tvOrderNumber.text = orderNumberString

        val orderDate = currentOrder.datetime.split("T")[0]
        holder.tvOrderDate.text = orderDate

        holder.tvOrderStatus.text = currentOrder.status

        val orderSum = currentOrder.items.fold(0.0) { acc, itemData ->
            acc + itemData.price * itemData.count
        }

        val orderSumString = "${"%.2f".format(orderSum)} руб."
        holder.tvOrderPrice.text = orderSumString

        val shopString = "г. ${currentOrder.shop.town}, ул. ${currentOrder.shop.street} ${currentOrder.shop.housing}"
        holder.tvOrderShop.text = shopString


        val itemsListInflater = LayoutInflater.from(holder.llDescriptionBookList.context)
        currentOrder.items.forEach { itemData ->
            val newItemView = itemsListInflater.inflate(R.layout.item_order_item_info, holder.llDescriptionBookList, false)

            newItemView.findViewById<TextView>(R.id.bookTitle).text = itemData.book.title
            val countString = "${itemData.count} шт."
            newItemView.findViewById<TextView>(R.id.bookCount).text = countString
            val priceString = "${"%.2f".format(itemData.price * itemData.count)} руб."
            newItemView.findViewById<TextView>(R.id.bookPrice).text = priceString

            holder.llDescriptionBookList.addView(newItemView)
        }

    }
}