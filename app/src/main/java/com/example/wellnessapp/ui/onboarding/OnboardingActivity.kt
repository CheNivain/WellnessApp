package com.example.wellnessapp.ui.onboarding

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.wellnessapp.MainActivity
import com.example.wellnessapp.R
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {
    
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var skipButton: MaterialButton
    private lateinit var nextButton: MaterialButton
    private lateinit var adapter: OnboardingAdapter
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)
        
        preferencesManager = PreferencesManager(this)
        
        // Check if onboarding is already completed
        if (preferencesManager.isOnboardingCompleted()) {
            navigateToMain()
            return
        }
        
        initViews()
        setupViewPager()
        setupClickListeners()
    }
    
    private fun initViews() {
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        skipButton = findViewById(R.id.skip_button)
        nextButton = findViewById(R.id.next_button)
    }
    
    private fun setupViewPager() {
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter
        
        // Connect TabLayout with ViewPager2
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()
        
        // Setup page change callback
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons(position)
            }
        })
    }
    
    private fun setupClickListeners() {
        skipButton.setOnClickListener {
            completeOnboarding()
        }
        
        nextButton.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem += 1
            } else {
                completeOnboarding()
            }
        }
    }
    
    private fun updateButtons(position: Int) {
        if (position == adapter.itemCount - 1) {
            nextButton.text = getString(R.string.get_started)
            skipButton.alpha = 0f
        } else {
            nextButton.text = getString(R.string.next)
            skipButton.alpha = 1f
        }
    }
    
    private fun completeOnboarding() {
        preferencesManager.setOnboardingCompleted(true)
        navigateToMain()
    }
    
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}