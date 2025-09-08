package com.example.fds2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.fds2.database.DatabaseHelper

class OrderDetailsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_order_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val orderId = arguments?.getLong("orderId") ?: return

        val (order, items) = DatabaseHelper(requireContext()).getOrderDetails(orderId)
            ?: return

        view.findViewById<TextView>(R.id.tvDetailName).text = order.customerName
        view.findViewById<TextView>(R.id.tvDetailAddress).text = order.address
        view.findViewById<TextView>(R.id.tvDetailPhone).text = order.phone
        view.findViewById<TextView>(R.id.tvDetailDate).text = order.date

        val tvItems = view.findViewById<TextView>(R.id.tvDetailItems)
        val sb = StringBuilder()
        items.forEach {
            sb.append("${it.itemName} ×${it.quantity} = ${it.quantity * it.price} Rs\n")
        }
        tvItems.text = sb.toString()

        view.findViewById<TextView>(R.id.tvDetailTotal).text = "Total: ${items.sumOf { it.quantity * it.price }} Rs"
    }
}
