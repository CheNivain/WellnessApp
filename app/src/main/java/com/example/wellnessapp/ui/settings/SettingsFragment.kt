package com.example.wellnessapp.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.wellnessapp.R
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.example.wellnessapp.ui.auth.LoginActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {
    
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var usernameText: TextView
    private lateinit var hydrationSwitch: SwitchMaterial
    private lateinit var intervalText: TextView
    private lateinit var changeIntervalButton: MaterialButton
    private lateinit var logoutButton: MaterialButton
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        preferencesManager = PreferencesManager(requireContext())
        
        initViews(view)
        setupClickListeners()
        loadSettings()
    }
    
    private fun initViews(view: View) {
        usernameText = view.findViewById(R.id.username_text)
        hydrationSwitch = view.findViewById(R.id.hydration_switch)
        intervalText = view.findViewById(R.id.interval_text)
        changeIntervalButton = view.findViewById(R.id.change_interval_button)
        logoutButton = view.findViewById(R.id.logout_button)
    }
    
    private fun setupClickListeners() {
        hydrationSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferencesManager.setHydrationEnabled(isChecked)
        }
        
        changeIntervalButton.setOnClickListener {
            showIntervalDialog()
        }
        
        logoutButton.setOnClickListener {
            showLogoutDialog()
        }
    }
    
    private fun loadSettings() {
        usernameText.text = preferencesManager.getCurrentUsername() ?: "User"
        hydrationSwitch.isChecked = preferencesManager.isHydrationEnabled()
        updateIntervalText()
    }
    
    private fun updateIntervalText() {
        val interval = preferencesManager.getHydrationInterval()
        intervalText.text = "$interval minutes"
    }
    
    private fun showIntervalDialog() {
        val intervals = arrayOf("15 minutes", "30 minutes", "60 minutes", "90 minutes", "120 minutes")
        val values = arrayOf(15, 30, 60, 90, 120)
        val currentInterval = preferencesManager.getHydrationInterval()
        val selectedIndex = values.indexOf(currentInterval).takeIf { it >= 0 } ?: 2
        
        AlertDialog.Builder(requireContext())
            .setTitle("Reminder Interval")
            .setSingleChoiceItems(intervals, selectedIndex) { dialog, which ->
                preferencesManager.setHydrationInterval(values[which])
                updateIntervalText()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                preferencesManager.logout()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}