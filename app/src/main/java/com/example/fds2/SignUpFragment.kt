package com.example.fds2

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fds2.database.DatabaseHelper

class SignUpFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_sign_up, c, false)

    private lateinit var signupUsername: EditText
    private lateinit var signupPhone: EditText
    private lateinit var signupEmail: EditText
    private lateinit var signupButton: Button
    private lateinit var signupTV: TextView
    private lateinit var dbHelper: DatabaseHelper


    override fun onViewCreated(v: View, s: Bundle?) {

        signupUsername = v.findViewById(R.id.edit_name)
        signupPhone = v.findViewById(R.id.edit_mobile_num)
        signupEmail = v.findViewById(R.id.edit_email)
        signupButton = v.findViewById(R.id.signUpBtn)
        signupTV = v.findViewById(R.id.signUpTV)

        signupButton.isEnabled = false

        dbHelper = DatabaseHelper(requireContext())

        var textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val name = signupUsername.text.toString().trim()
                val phone = signupPhone.text.toString().trim()
                val mail = signupEmail.text.toString().trim()

                signupButton.isEnabled =
                    name.isNotEmpty() && phone.isNotEmpty() && mail.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        signupUsername.addTextChangedListener(textWatcher)
        signupPhone.addTextChangedListener(textWatcher)
        signupEmail.addTextChangedListener(textWatcher)

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.signupFragment, true) // This removes SignUp from back stack
            .build()

        signupTV.setOnClickListener {
            findNavController().navigate(R.id.action_signup_to_login,null, navOptions)
        }

        signupButton.setOnClickListener {
            val username = signupUsername.text.toString().trim()
            val phone = signupPhone.text.toString().trim()
            val email = signupEmail.text.toString().trim()

            // Show loading dialog
            val loadingView = layoutInflater.inflate(R.layout.progress_bar, null)
            loadingView.findViewById<TextView>(R.id.tvLoadingMessage).text = "Signing up..."

            val loadingDialog = Dialog(requireContext())
            loadingDialog.setContentView(loadingView)
            loadingDialog.setCancelable(false)
            loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            loadingDialog.show()

            // Simulate delay or run DB logic immediately (ideally move this to coroutine/thread)
            Handler(Looper.getMainLooper()).postDelayed({
                val result = dbHelper.insertUser(username, phone, email)

                if (result != -1L) {
                    dbHelper.loginUser(username, phone)

                    val bundle = Bundle().apply {
                        putString("username", username)
                        putString("phone", phone)
                        putString("email", email)
                    }

                    loadingDialog.dismiss()
                    findNavController().navigate(R.id.action_signup_to_home, bundle, navOptions)
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Signup Failed", Toast.LENGTH_SHORT).show()
                }
            }, 7000) // Optional: 1s delay to simulate loading
        }

    }
}

