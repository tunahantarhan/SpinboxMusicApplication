package com.example.spinboxmusicapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class Order(
    val productTitle: String = "",
    val orderDate: Long = 0L,
    val price: Double = 0.0,
    val quantity: Int = 1,
    val uid : String = ""
)
class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.productTitleTextView.text = order.productTitle
        if (order.uid.toString() == ""){
            holder.orderUidTextView.text = "Siz"
        }
        else{
            holder.orderUidTextView.text = "UID : ${order.uid}"
        }
        holder.quantityTextView.text = "Adet : ${order.quantity}"
        holder.priceTextView.text = "Fiyat (Adet) : ${order.price} ₺"
        holder.orderDateTextView.text = "${formatDate(order.orderDate)}"
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    private fun formatDate(millis: Long): String {
        val date = Date(millis)
        val formatter = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
        return formatter.format(date)
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderUidTextView: TextView = itemView.findViewById(R.id.orderUidText)
        val productTitleTextView: TextView = itemView.findViewById(R.id.productTitleTextView)
        val priceTextView: TextView = itemView.findViewById(R.id.priceTextView)
        val quantityTextView: TextView = itemView.findViewById(R.id.quantityTextView)
        val orderDateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
    }
}