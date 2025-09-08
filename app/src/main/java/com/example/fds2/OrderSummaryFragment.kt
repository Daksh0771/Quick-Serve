package com.example.fds2

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fds2.database.DatabaseHelper

class OrderSummaryFragment : Fragment() {

    private lateinit var tvOrderList: LinearLayout
    private lateinit var etName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var btnOrder: Button

    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_order_summary, c, false)

    private fun refreshUI() {
        onViewCreated(requireView(), null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        tvOrderList = view.findViewById(R.id.tvOrderList)
        etName = view.findViewById(R.id.edit_fullname)
        etAddress = view.findViewById(R.id.edit_address)
        etPhone = view.findViewById(R.id.edit_phone)
        btnOrder = view.findViewById(R.id.btnMainOrder)


        tvOrderList.removeAllViews() // Clear previous views

        if (CartManager.cartItems.isEmpty()) {
            val emptyText = TextView(requireContext()).apply {
                text = "No Items Placed"
                textSize = 16f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
            }
            tvOrderList.addView(emptyText)
        } else {
            // Loop through cart items
            CartManager.cartItems.forEach { (itemName, pair) ->
                val (quantity, pricePerUnit) = pair
                val itemTotal = quantity * pricePerUnit

                val itemLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, 16, 0, 16)
                }

                val itemHeader = TextView(requireContext()).apply {
                    text = "$itemName = $itemTotal Rs"
                    textSize = 16f
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
                }

                val controlLayout = LinearLayout(requireContext()).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(0, 8, 0, 8)
                }

                val minusButton = Button(requireContext()).apply {
                    text = "–"
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.button_res)
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
                    textSize = 26f
                    gravity = Gravity.CENTER
                    setTextColor(Color.WHITE)
                    setPadding(2)
                    setOnClickListener {
                        CartManager.removeItems(itemName)
                        refreshUI()
                    }
                }


                val quantityText = TextView(requireContext()).apply {
                    text = quantity.toString()
                    textSize = 16f
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
                    setPadding(16, 0, 16, 0)
                }

                val plusButton = Button(requireContext()).apply {
                    text = "+"
                    background = ContextCompat.getDrawable(requireContext(), R.drawable.button_res)
                    typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
                    textSize = 26f
                    setTextColor(Color.WHITE)
                    gravity = Gravity.CENTER
                    setPadding(2)
                    setOnClickListener {
                        CartManager.addItems(itemName, pricePerUnit)
                        refreshUI()
                    }
                }

                controlLayout.addView(minusButton)
                controlLayout.addView(quantityText)
                controlLayout.addView(plusButton)

                itemLayout.addView(itemHeader)
                itemLayout.addView(controlLayout)

                tvOrderList.addView(itemLayout)
            }

            val totalText = TextView(requireContext()).apply {
                text = "\nTotal: ${CartManager.getTotal()} Rs"
                textSize = 18f
                typeface = ResourcesCompat.getFont(requireContext(), R.font.mark_pro_bold)
                setPadding(0, 16, 0, 0)
            }

            tvOrderList.addView(totalText)
        }

        var textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val fullname = etName.text.toString().trim()
                val fulladdress = etAddress.text.toString().trim()
                val mobile = etPhone.text.toString().trim()

                btnOrder.isEnabled = fullname.isNotEmpty() && fulladdress.isNotEmpty()
                        && mobile.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        etName.addTextChangedListener(textWatcher)
        etAddress.addTextChangedListener(textWatcher)
        etPhone.addTextChangedListener(textWatcher)

        btnOrder.setOnClickListener {
            val name = etName.text.toString()
            val address = etAddress.text.toString()
            val phone = etPhone.text.toString()

            val dbHelper = DatabaseHelper(requireContext())
            val user = dbHelper.getLoggedInUser()

            if (user != null) {
                val result = dbHelper.insertOrder(
                    user.id,
                    name,
                    address,
                    phone,
                    CartManager.cartItems
                )

                if (result != -1L) {
                    CartManager.clearCart()
                }

                val bundle = Bundle().apply {
                    putString("name", name)
                    putString("address", address)
                    putString("phone", phone)
                }

                findNavController().navigate(R.id.action_order_to_splash, bundle)
            }
        }
    }
}