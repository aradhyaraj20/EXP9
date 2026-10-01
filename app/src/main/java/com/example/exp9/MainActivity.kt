package com.example.exp9

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var etUsername: MaterialAutoCompleteTextView
    private lateinit var etPassword: TextInputEditText
    private lateinit var cbRemember: CheckBox
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvAutofillStatus: TextView

    companion object {
        private const val PREF_NAME = "UserPrefs"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_REMEMBER = "remember_me"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize SQLite helper and SharedPreferences
        dbHelper = DatabaseHelper(this)
        sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // Initialize UI elements
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        cbRemember = findViewById(R.id.cbRemember)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)
        tvAutofillStatus = findViewById(R.id.tvAutofillStatus)

        // Setup AutoComplete suggestions from SQLite Database
        setupAutoCompleteSuggestions()

        // Handle initial load or logout intent
        checkIntentAndAutofill(intent)

        // Set up button event listeners
        btnLogin.setOnClickListener {
            handleLoginOrRegister(isRegistration = false)
        }

        btnRegister.setOnClickListener {
            handleLoginOrRegister(isRegistration = true)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        checkIntentAndAutofill(intent)
    }

    override fun onResume() {
        super.onResume()
        setupAutoCompleteSuggestions()
        if (intent.getBooleanExtra("EXTRA_LOGGED_OUT", false)) {
            clearLoginForm()
        }
    }

    private fun checkIntentAndAutofill(currentIntent: Intent) {
        val isLoggedOut = currentIntent.getBooleanExtra("EXTRA_LOGGED_OUT", false)
        if (isLoggedOut) {
            clearLoginForm()
            tvAutofillStatus.text = "Logged out. Details removed. Start typing for database suggestions."
            tvAutofillStatus.setTextColor(getColor(android.R.color.darker_gray))
        } else {
            autofillFromSharedPreferences()
        }
    }

    private fun clearLoginForm() {
        etUsername.setText("")
        etPassword.setText("")
    }

    /**
     * Sets up AutoComplete suggestions on etUsername using distinct usernames from SQLite database.
     */
    private fun setupAutoCompleteSuggestions() {
        val dbUsernames = dbHelper.getAllUsernames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, dbUsernames)
        etUsername.setAdapter(adapter)

        etUsername.setOnItemClickListener { parent, _, position, _ ->
            val selectedUsername = parent.getItemAtPosition(position) as String
            val savedPassword = dbHelper.getPasswordForUser(selectedUsername)
            if (!savedPassword.isNullOrEmpty()) {
                etPassword.setText(savedPassword)
                tvAutofillStatus.text = "Autofilled credentials for $selectedUsername from SQLite DB"
                tvAutofillStatus.setTextColor(getColor(android.R.color.holo_green_dark))
            }
        }
    }

    /**
     * Loads saved credentials from SharedPreferences on app startup.
     */
    private fun autofillFromSharedPreferences() {
        val isRemembered = sharedPreferences.getBoolean(KEY_REMEMBER, true)
        val savedUsername = sharedPreferences.getString(KEY_USERNAME, "")
        val savedPassword = sharedPreferences.getString(KEY_PASSWORD, "")

        cbRemember.isChecked = isRemembered

        if (!savedUsername.isNullOrEmpty() && !savedPassword.isNullOrEmpty()) {
            etUsername.setText(savedUsername)
            etPassword.setText(savedPassword)
            tvAutofillStatus.text = "Autofilled from SharedPreferences ($savedUsername)"
            tvAutofillStatus.setTextColor(getColor(android.R.color.holo_green_dark))
        } else {
            tvAutofillStatus.text = "Start typing for database suggestions"
            tvAutofillStatus.setTextColor(getColor(android.R.color.darker_gray))
        }
    }

    /**
     * Saves or removes credentials in SharedPreferences.
     */
    private fun saveToSharedPreferences(username: String, password: String, remember: Boolean) {
        val editor = sharedPreferences.edit()
        if (remember) {
            editor.putString(KEY_USERNAME, username)
            editor.putString(KEY_PASSWORD, password)
            editor.putBoolean(KEY_REMEMBER, true)
        } else {
            editor.remove(KEY_USERNAME)
            editor.remove(KEY_PASSWORD)
            editor.putBoolean(KEY_REMEMBER, false)
        }
        editor.apply()
    }

    /**
     * Handles login or registration logic, saving to SQLite DB and SharedPreferences, then opening Page 2.
     */
    private fun handleLoginOrRegister(isRegistration: Boolean) {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()
        val remember = cbRemember.isChecked

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Save / Insert user into SQLite Database
        val success = dbHelper.insertUser(username, password)

        if (success) {
            // Save to SharedPreferences for autofill
            saveToSharedPreferences(username, password, remember)

            val actionMsg = if (isRegistration) "Registered in SQLite! Redirecting..." else "Logged in! Redirecting..."
            Toast.makeText(this, actionMsg, Toast.LENGTH_SHORT).show()

            // Reset logout flag so next intent is clean
            intent.putExtra("EXTRA_LOGGED_OUT", false)

            // Navigate to Page 2 (HomeActivity)
            val nextIntent = Intent(this, HomeActivity::class.java).apply {
                putExtra("EXTRA_USERNAME", username)
            }
            startActivity(nextIntent)
        } else {
            Toast.makeText(this, "Failed to save user in SQLite DB", Toast.LENGTH_SHORT).show()
        }
    }
}
