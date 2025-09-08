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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fds2.database.DatabaseHelper

class OrderHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderHistoryAdapter
    private lateinit var label: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_order_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DatabaseHelper(requireContext())
        label = view.findViewById(R.id.labelOrderHistory)

        val user = dbHelper.getLoggedInUser() // Assuming this returns User object with id and name

        if (user != null) {
            label.text = "${user.name}'s Order History"

            recyclerView = view.findViewById(R.id.rvOrderHistory)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Pass user.id to get orders for that user only
            val orders = dbHelper.getAllOrders(user.id)

            adapter = OrderHistoryAdapter(orders) { order ->
                val bundle = Bundle().apply { putLong("orderId", order.orderId) }
                findNavController().navigate(R.id.action_orderHistory_to_orderDetail, bundle)
            }
            recyclerView.adapter = adapter
        } else {
            label.text = "Your Order History"
            // Handle case when no user is logged in (empty list or prompt login)
            recyclerView.adapter = OrderHistoryAdapter(emptyList()) {}
        }
    }
}
