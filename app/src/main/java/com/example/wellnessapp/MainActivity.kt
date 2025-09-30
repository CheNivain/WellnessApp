package com.example.wellnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.example.wellnessapp.sensors.ShakeDetector
import com.example.wellnessapp.sensors.StepCounter
import com.example.wellnessapp.ui.auth.LoginActivity
import com.example.wellnessapp.ui.habits.HabitsFragment
import com.example.wellnessapp.ui.hydration.HydrationFragment
import com.example.wellnessapp.ui.mood.AddMoodDialogFragment
import com.example.wellnessapp.ui.mood.MoodFragment
import com.example.wellnessapp.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var stepCounter: StepCounter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferencesManager = PreferencesManager(this)
        
        // Check if user is logged in
        if (!preferencesManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupBottomNavigation()
        setupSensors()
        
        // Show habits fragment by default
        if (savedInstanceState == null) {
            replaceFragment(HabitsFragment())
        }
    }
    
    private fun setupBottomNavigation() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_habits -> {
                    replaceFragment(HabitsFragment())
                    true
                }
                R.id.nav_mood -> {
                    replaceFragment(MoodFragment())
                    true
                }
                R.id.nav_hydration -> {
                    replaceFragment(HydrationFragment())
                    true
                }
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    true
                }
                else -> false
            }
        }
    }
    
    private fun setupSensors() {
        // Setup shake detection for quick mood logging
        shakeDetector = ShakeDetector(this) {
            showQuickMoodDialog()
        }
        
        // Setup step counter
        stepCounter = StepCounter(this) { steps ->
            // Step count updated - could show in notification or update UI
        }
    }
    
    private fun showQuickMoodDialog() {
        Toast.makeText(this, getString(R.string.shake_to_log_mood), Toast.LENGTH_SHORT).show()
        val dialog = AddMoodDialogFragment { moodEntry ->
            preferencesManager.addMoodEntry(moodEntry)
            Toast.makeText(this, "Mood logged! ${moodEntry.emoji}", Toast.LENGTH_SHORT).show()
        }
        dialog.show(supportFragmentManager, "QuickMoodDialog")
    }
    
    override fun onResume() {
        super.onResume()
        shakeDetector.start()
        stepCounter.start()
    }
    
    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
        stepCounter.stop()
    }
    
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}