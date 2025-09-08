package com.example.fds2

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fds2.database.DatabaseHelper

class ProfileFragment : Fragment() {
    override fun onCreateView(i: LayoutInflater, c: ViewGroup?, s: Bundle?) =
        i.inflate(R.layout.fragment_profile, c, false)

    private lateinit var txtUser: TextView
    private lateinit var txtEmail: TextView
    private lateinit var logOut: LinearLayout
    private lateinit var history: LinearLayout
    private lateinit var delete: LinearLayout


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtUser = view.findViewById(R.id.txtUser)
        txtEmail = view.findViewById(R.id.txtEmail)
        logOut = view.findViewById(R.id.btnLogOut)
        history = view.findViewById(R.id.btnOrderHistory)
        delete = view.findViewById(R.id.btnDeleteAccount)

        history.setOnClickListener{
            findNavController().navigate(R.id.action_profile_to_orderHistory)
        }

        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)

        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)
        dialogView.findViewById<TextView>(R.id.dialogTitle).text = "Log Out?"
        dialogView.findViewById<TextView>(R.id.dialogMessage).text = "Are you sure you want to log out?"
        dialogView.findViewById<TextView>(R.id.dialogMessage2).text = "You can log back in later"

        val alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        logOut.setOnClickListener {
            btnYes.setOnClickListener {
                val loadingView = layoutInflater.inflate(R.layout.progress_bar, null)
                loadingView.findViewById<TextView>(R.id.tvLoadingMessage).text = "Logging out..."

                val loadingDialog = Dialog(requireContext())
                loadingDialog.setContentView(loadingView)
                loadingDialog.setCancelable(false)
                loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog.show()

                alertDialog.dismiss()

                Handler(Looper.getMainLooper()).postDelayed({
                val dbHelper = DatabaseHelper(requireContext())
                dbHelper.logoutUser()

                loadingDialog.dismiss()

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()

                findNavController().navigate(R.id.action_profile_to_login, null, navOptions)

            }, 3000)
            }

            btnNo.setOnClickListener {
                alertDialog.dismiss()
            }

            alertDialog.show()
        }

        delete.setOnClickListener{

            val dialogView2 = layoutInflater.inflate(R.layout.alert_dialog, null)

            val dialogBuilder2 = AlertDialog.Builder(requireContext())
                .setView(dialogView2)
                .setCancelable(false)

            val btnYes2 = dialogView2.findViewById<Button>(R.id.btnYes)
            val btnNo2 = dialogView2.findViewById<Button>(R.id.btnNo)

            dialogView2.findViewById<TextView>(R.id.dialogTitle).text = "Delete Account?"
            dialogView2.findViewById<TextView>(R.id.dialogMessage).text = "Are you sure you want to Delete Account?"
            dialogView2.findViewById<TextView>(R.id.dialogMessage2).text = "This will erase all your data"

            val alertDialog2 = dialogBuilder2.create()
            alertDialog2.window?.setBackgroundDrawableResource(android.R.color.transparent)

            btnYes2.setOnClickListener {
                val loadingView2 = layoutInflater.inflate(R.layout.progress_bar, null)
                loadingView2.findViewById<TextView>(R.id.tvLoadingMessage).text = "Deleting account..."

                val loadingDialog2 = Dialog(requireContext())
                loadingDialog2.setContentView(loadingView2)
                loadingDialog2.setCancelable(false)
                loadingDialog2.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                loadingDialog2.show()

                alertDialog2.dismiss()

                val dbHelper = DatabaseHelper(requireContext())
                val user = dbHelper.getLoggedInUser()!! // Assumes a user is logged in

                Handler(Looper.getMainLooper()).postDelayed({
                dbHelper.deleteUserAndAllData(user.id, user.name, user.phone)

                    loadingDialog2.dismiss()

                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.nav_graph, true)
                    .build()

                findNavController().navigate(R.id.action_profile_to_signup, null, navOptions)

            }, 3000)
            }
            btnNo2.setOnClickListener {
                alertDialog2.dismiss()
            }

            alertDialog2.show()
        }

            val dbHelper = DatabaseHelper(requireContext())
            val user = dbHelper.getLoggedInUsername()

            if (user != null) {
                val username = user.first
                val email = user.second

                txtUser.text = username
                txtEmail.text = email
            }
        }
    }


