package com.example.fds2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fds2.database.DatabaseHelper

class OrderHistoryAdapter(
    private val orders: List<DatabaseHelper.Order>,
    private val onClick: (DatabaseHelper.Order) -> Unit
) : RecyclerView.Adapter<OrderHistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderLabel: TextView = itemView.findViewById(R.id.tvOrderLabel)
        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) onClick(orders[position])
            }
        }
    }

    class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnTouchListener { v, event ->
                v.parent.requestDisallowInterceptTouchEvent(true)
                false
            }
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.tvOrderLabel.text = "Order ${position + 1}"
    }

    override fun getItemCount() = orders.size

}

