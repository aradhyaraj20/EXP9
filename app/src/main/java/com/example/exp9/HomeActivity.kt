package com.example.exp9

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton

class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    private lateinit var tvWelcomeUser: TextView
    private lateinit var tvDatabaseRecords: TextView
    private lateinit var btnLogout: MaterialButton
    private lateinit var btnRefresh: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.mainHome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)

        tvWelcomeUser = findViewById(R.id.tvWelcomeUser)
        tvDatabaseRecords = findViewById(R.id.tvDatabaseRecords)
        btnLogout = findViewById(R.id.btnLogout)
        btnRefresh = findViewById(R.id.btnRefresh)

        // Retrieve logged-in username passed from MainActivity
        val username = intent.getStringExtra("EXTRA_USERNAME") ?: "User"
        tvWelcomeUser.text = "Welcome, $username!"

        // Display all records stored in SQLite Database
        refreshDatabaseRecords()

        // Handle Logout button click
        btnLogout.setOnClickListener {
            Toast.makeText(this, "Logged out. Credentials cleared.", Toast.LENGTH_SHORT).show()
            val logoutIntent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("EXTRA_LOGGED_OUT", true)
            }
            startActivity(logoutIntent)
            finish()
        }

        // Handle Refresh button click
        btnRefresh.setOnClickListener {
            refreshDatabaseRecords()
            Toast.makeText(this, "SQLite Records refreshed", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Retrieves all user entries from SQLite Database and updates the UI text.
     */
    private fun refreshDatabaseRecords() {
        val users = dbHelper.getAllUsers()
        if (users.isEmpty()) {
            tvDatabaseRecords.text = "No users currently stored in SQLite database."
        } else {
            val dbText = StringBuilder()
            dbText.append("Total Accounts in SQLite: ${users.size}\n\n")
            users.forEach { userRecord ->
                dbText.append(userRecord).append("\n")
            }
            tvDatabaseRecords.text = dbText.toString()
        }
    }
}
