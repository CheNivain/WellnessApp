package com.example.wellnessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.example.wellnessapp.data.database.AppDatabase
import com.example.wellnessapp.data.database.DataMigrationHelper
import com.example.wellnessapp.data.database.entities.MoodEntity
import com.example.wellnessapp.sensors.ShakeDetector
import com.example.wellnessapp.sensors.StepCounter
import com.example.wellnessapp.ui.auth.LoginActivity
import com.example.wellnessapp.ui.habits.HabitsFragment
import com.example.wellnessapp.ui.hydration.HydrationFragment
import com.example.wellnessapp.ui.mood.AddMoodDialogFragment
import com.example.wellnessapp.ui.mood.MoodFragment
import com.example.wellnessapp.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var database: AppDatabase
    private var bottomNavigation: BottomNavigationView? = null
    private var navigationRail: com.google.android.material.navigationrail.NavigationRailView? = null
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var stepCounter: StepCounter
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferencesManager = PreferencesManager(this)
        database = AppDatabase.getDatabase(this)
        
        // Migrate data from SharedPreferences to Room if needed
        lifecycleScope.launch {
            DataMigrationHelper.migrateDataIfNeeded(this@MainActivity)
        }
        
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
        navigationRail = findViewById(R.id.navigation_rail)
        
        val itemSelectedListener = { item: android.view.MenuItem ->
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
        
        bottomNavigation?.setOnItemSelectedListener(itemSelectedListener)
        navigationRail?.setOnItemSelectedListener(itemSelectedListener)
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
            lifecycleScope.launch {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(moodEntry.timestamp))
                database.moodDao().insert(
                    MoodEntity(
                        emoji = moodEntry.emoji,
                        description = moodEntry.moodName,
                        moodValue = moodEntry.moodValue,
                        date = date,
                        timestamp = moodEntry.timestamp
                    )
                )
                Toast.makeText(this@MainActivity, "Mood logged! ${moodEntry.emoji}", Toast.LENGTH_SHORT).show()
            }
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