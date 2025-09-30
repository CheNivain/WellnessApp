package com.example.wellnessapp.ui.hydration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.wellnessapp.R
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.*

class HydrationFragment : Fragment() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var waterCountText: TextView
    private lateinit var progressIndicator: LinearProgressIndicator
    private lateinit var drinkWaterButton: MaterialButton
    private lateinit var resetButton: MaterialButton
    
    private val DAILY_GOAL = 8 // 8 glasses of water
    private var currentGlasses = 0
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_hydration, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        
        initViews(view)
        setupClickListeners()
        loadWaterIntake()
    }
    
    private fun initViews(view: View) {
        waterCountText = view.findViewById(R.id.water_count_text)
        progressIndicator = view.findViewById(R.id.progress_indicator)
        drinkWaterButton = view.findViewById(R.id.drink_water_button)
        resetButton = view.findViewById(R.id.reset_button)
    }
    
    private fun setupClickListeners() {
        drinkWaterButton.setOnClickListener {
            addWaterGlass()
        }
        
        resetButton.setOnClickListener {
            resetWaterIntake()
        }
    }
    
    private fun loadWaterIntake() {
        // Get today's water intake from preferences
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val key = "water_intake_$today"
        currentGlasses = requireContext().getSharedPreferences("wellness_app_prefs", 0)
            .getInt(key, 0)
        updateUI()
    }
    
    private fun addWaterGlass() {
        if (currentGlasses < DAILY_GOAL) {
            currentGlasses++
            saveWaterIntake()
            updateUI()
        }
    }
    
    private fun resetWaterIntake() {
        currentGlasses = 0
        saveWaterIntake()
        updateUI()
    }
    
    private fun saveWaterIntake() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val key = "water_intake_$today"
        requireContext().getSharedPreferences("wellness_app_prefs", 0)
            .edit()
            .putInt(key, currentGlasses)
            .apply()
    }
    
    private fun updateUI() {
        waterCountText.text = "$currentGlasses / $DAILY_GOAL glasses"
        val progress = (currentGlasses * 100) / DAILY_GOAL
        progressIndicator.progress = progress
        
        drinkWaterButton.isEnabled = currentGlasses < DAILY_GOAL
        
        if (currentGlasses >= DAILY_GOAL) {
            waterCountText.append(" ✅")
        }
    }
}