package com.example.wellnessapp.ui.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    private val onboardingData = listOf(
        OnboardingData(
            "Track Your Habits",
            "Build and maintain healthy habits with our intuitive daily tracker",
            "🎯"
        ),
        OnboardingData(
            "Journal Your Mood",
            "Express your feelings with emojis and track your emotional wellbeing",
            "😊"
        ),
        OnboardingData(
            "Stay Hydrated",
            "Get timely reminders to drink water and maintain optimal hydration",
            "💧"
        ),
        OnboardingData(
            "Monitor Progress",
            "Visualize your wellness journey with comprehensive tracking and insights",
            "📊"
        )
    )
    
    override fun getItemCount(): Int = onboardingData.size
    
    override fun createFragment(position: Int): Fragment {
        return OnboardingFragment.newInstance(onboardingData[position])
    }
}

data class OnboardingData(
    val title: String,
    val description: String,
    val emoji: String
) : java.io.Serializable