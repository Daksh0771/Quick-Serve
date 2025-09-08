package com.example.fds2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fds2.database.DatabaseHelper

class LoginFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_login, c, false)


    private lateinit var loginUsername: EditText
    private lateinit var loginMobile: EditText
    private lateinit var loginButton: Button
    private lateinit var loginTV: TextView
    private lateinit var dbhelper : DatabaseHelper

    override fun onViewCreated(v: View, s: Bundle?) {

        loginUsername = v.findViewById(R.id.edit_username)
        loginMobile = v.findViewById(R.id.edit_mobile)
        loginButton = v.findViewById(R.id.loginBtn)
        loginTV = v.findViewById(R.id.loginTV)

        dbhelper = DatabaseHelper(requireContext())

        loginButton.isEnabled = false

        var textWatcher = object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {
                val username = loginUsername.text.toString().trim()
                val mobile = loginMobile.text.toString().trim()

                loginButton.isEnabled = username.isNotEmpty() && mobile.isNotEmpty()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        loginUsername.addTextChangedListener(textWatcher)
        loginMobile.addTextChangedListener(textWatcher)

       val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.loginFragment, true) // Clear login from back stack
            .build()

        loginButton.setOnClickListener {
            val username = loginUsername.text.toString().trim()
            val phone = loginMobile.text.toString().trim()


            //findNavController().navigate(R.id.action_login_to_home)

            if (dbhelper.checkUser(username, phone)) {
                dbhelper.loginUser(username, phone)
                // Get email directly from DB without modifying dbHelper
                val db = dbhelper.readableDatabase
                val cursor = db.rawQuery(
                    "SELECT email FROM users WHERE username = ?",
                    arrayOf(username)
                )
                var email: String? = null
                if (cursor.moveToFirst()) {
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                }
                cursor.close()
                db.close()

                val bundle = Bundle().apply {
                    putString("username", username)
                    putString("phone", phone)
                    putString("email", email)  // pass email here!
                }

                findNavController().navigate(R.id.action_login_to_home, bundle, navOptions)
            }
            else{
                Toast.makeText(context, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
            }
        }
        loginTV.setOnClickListener{
            findNavController().navigate(R.id.action_login_to_signup,null, navOptions)
        }
    }
    }
