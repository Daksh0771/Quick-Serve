package com.example.fds2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController


class AdminPanelFragment : Fragment() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginAdminButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_admin_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        usernameEditText = view.findViewById(R.id.edit_adminUser)
        passwordEditText = view.findViewById(R.id.edit_adminPass)
        loginAdminButton = view.findViewById(R.id.loginAdminBtn)

        loginAdminButton.isEnabled = false

        var textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val username = usernameEditText.text.toString().trim()
                val password = passwordEditText.text.toString().trim()

                loginAdminButton.isEnabled = username.isNotEmpty() && password.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        usernameEditText.addTextChangedListener(textWatcher)
        passwordEditText.addTextChangedListener(textWatcher)

        loginAdminButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            // Hardcoded admin credentials
            if (username == "admin" && password == "admin123") {

                findNavController().navigate(R.id.action_adminLoginFragment_to_adminPanelFragment)
            } else {
                Toast.makeText(requireContext(), "Invalid admin credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
