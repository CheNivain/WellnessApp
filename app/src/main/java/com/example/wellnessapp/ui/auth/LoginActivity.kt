package com.example.wellnessapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.example.wellnessapp.MainActivity
import com.example.wellnessapp.R
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.example.wellnessapp.ui.onboarding.OnboardingActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var usernameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var usernameEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: MaterialButton
    private lateinit var signupButton: MaterialButton
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        preferencesManager = PreferencesManager(this)
        
        // Check if already logged in
        if (preferencesManager.isLoggedIn()) {
            navigateToMainFlow()
            return
        }
        
        initViews()
        setupClickListeners()
        setupTextWatchers()
    }
    
    private fun initViews() {
        usernameLayout = findViewById(R.id.username_layout)
        passwordLayout = findViewById(R.id.password_layout)
        usernameEditText = findViewById(R.id.username_edit_text)
        passwordEditText = findViewById(R.id.password_edit_text)
        loginButton = findViewById(R.id.login_button)
        signupButton = findViewById(R.id.signup_button)
    }
    
    private fun setupClickListeners() {
        loginButton.setOnClickListener {
            attemptLogin()
        }
        
        signupButton.setOnClickListener {
            attemptSignup()
        }
    }
    
    private fun setupTextWatchers() {
        usernameEditText.addTextChangedListener {
            usernameLayout.error = null
        }
        
        passwordEditText.addTextChangedListener {
            passwordLayout.error = null
        }
    }
    
    private fun attemptLogin() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        if (validateInput(username, password)) {
            if (preferencesManager.login(username, password)) {
                navigateToMainFlow()
            } else {
                passwordLayout.error = "Invalid username or password"
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun attemptSignup() {
        val username = usernameEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()
        
        if (validateInput(username, password)) {
            if (preferencesManager.signup(username, password)) {
                Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                navigateToMainFlow()
            } else {
                Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun validateInput(username: String, password: String): Boolean {
        var isValid = true
        
        if (username.isEmpty()) {
            usernameLayout.error = "Username is required"
            isValid = false
        } else if (username.length < 3) {
            usernameLayout.error = "Username must be at least 3 characters"
            isValid = false
        }
        
        if (password.isEmpty()) {
            passwordLayout.error = "Password is required"
            isValid = false
        } else if (password.length < 6) {
            passwordLayout.error = "Password must be at least 6 characters"
            isValid = false
        }
        
        return isValid
    }
    
    private fun navigateToMainFlow() {
        val intent = if (preferencesManager.isOnboardingCompleted()) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, OnboardingActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}