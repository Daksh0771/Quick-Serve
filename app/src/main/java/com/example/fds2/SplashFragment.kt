package com.example.fds2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

class SplashFragment : Fragment() {
    private lateinit var progressBar: ProgressBar
    private lateinit var tvOrderDetails: TextView
    private lateinit var imgSuccess: ImageView
    private lateinit var tvOrderPlaced: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar = view.findViewById(R.id.progressBar)
        tvOrderDetails = view.findViewById(R.id.tvOrderDetails)
        imgSuccess = view.findViewById(R.id.imgSuccess)
        tvOrderPlaced = view.findViewById(R.id.tvOrderPlaced)

        val name = arguments?.getString("name") ?: ""
        val address = arguments?.getString("address") ?: ""
        val phone = arguments?.getString("phone") ?: ""

        tvOrderDetails.text = "Confirming order for:\n$name\nDelivering to:\n$address\nContact: $phone"

        // 1st delay -> Show loading (already default)
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            tvOrderDetails.visibility = View.GONE
            imgSuccess.visibility = View.VISIBLE
            tvOrderPlaced.visibility = View.VISIBLE
        }, 4000) // 4 sec

        // 2nd delay -> Navigate back to Home
        Handler(Looper.getMainLooper()).postDelayed({
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.nav_graph, true) // This removes SplashFragment from the back stack
                .build()
            CartManager.clearCart()
            findNavController().navigate(R.id.action_splash_to_home,null, navOptions)
        }, 8000) // 8 sec total (4 sec loading + 4 sec success)
    }
}