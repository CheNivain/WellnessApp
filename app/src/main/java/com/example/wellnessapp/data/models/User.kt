package com.example.wellnessapp.data.models

data class User(
    val username: String,
    val email: String = "",
    val hasCompletedOnboarding: Boolean = false,
    val hydrationReminderInterval: Int = 60, // minutes
    val isHydrationReminderEnabled: Boolean = true,
    val dailyStepGoal: Int = 10000
)