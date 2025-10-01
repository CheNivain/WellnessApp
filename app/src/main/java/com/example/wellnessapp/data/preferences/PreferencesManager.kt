package com.example.wellnessapp.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.example.wellnessapp.data.models.Habit
import com.example.wellnessapp.data.models.MoodEntry
import com.example.wellnessapp.data.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val PREFS_NAME = "wellness_app_prefs"

        // Auth keys
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_USER_EMAIL = "user_email"

        // Onboarding
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"

        // Habits
        private const val KEY_HABITS = "habits"

        // Moods
        private const val KEY_MOOD_ENTRIES = "mood_entries"

        // Settings
        private const val KEY_HYDRATION_INTERVAL = "hydration_interval"
        private const val KEY_HYDRATION_ENABLED = "hydration_enabled"
        private const val KEY_STEP_GOAL = "step_goal"

        // Steps
        private const val KEY_DAILY_STEPS = "daily_steps"
        private const val KEY_LAST_STEP_DATE = "last_step_date"
    }

    // Authentication
    fun login(username: String, password: String): Boolean {
        val savedPassword = prefs.getString(KEY_PASSWORD, null)
        val savedUsername = prefs.getString(KEY_USERNAME, null)

        return if (savedUsername == null) {
            // First time login, save credentials
            prefs.edit()
                .putString(KEY_USERNAME, username)
                .putString(KEY_PASSWORD, password)
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .apply()
            true
        } else {
            // Verify credentials
            if (savedUsername == username && savedPassword == password) {
                prefs.edit().putBoolean(KEY_IS_LOGGED_IN, true).apply()
                true
            } else {
                false
            }
        }
    }

    fun logout() {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, false).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getCurrentUsername(): String? = prefs.getString(KEY_USERNAME, null)

    // Onboarding
    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)

    // Habits
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        prefs.edit().putString(KEY_HABITS, json).apply()
    }

    fun getHabits(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addHabit(habit: Habit) {
        val habits = getHabits().toMutableList()
        habits.add(habit)
        saveHabits(habits)
    }

    fun updateHabit(updatedHabit: Habit) {
        val habits = getHabits().toMutableList()
        val index = habits.indexOfFirst { it.id == updatedHabit.id }
        if (index != -1) {
            habits[index] = updatedHabit
            saveHabits(habits)
        }
    }

    fun deleteHabit(habitId: String) {
        val habits = getHabits().toMutableList()
        habits.removeIf { it.id == habitId }
        saveHabits(habits)
    }

    // Mood entries
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        prefs.edit().putString(KEY_MOOD_ENTRIES, json).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = prefs.getString(KEY_MOOD_ENTRIES, null) ?: return emptyList()
        val type = object : TypeToken<List<MoodEntry>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addMoodEntry(moodEntry: MoodEntry) {
        val entries = getMoodEntries().toMutableList()
        entries.add(moodEntry)
        saveMoodEntries(entries)
    }
    
    fun deleteMoodEntry(moodId: String) {
        val entries = getMoodEntries().toMutableList()
        entries.removeIf { it.id == moodId }
        saveMoodEntries(entries)
    }

    // Settings
    fun setHydrationInterval(minutes: Int) {
        prefs.edit().putInt(KEY_HYDRATION_INTERVAL, minutes).apply()
    }

    fun getHydrationInterval(): Int = prefs.getInt(KEY_HYDRATION_INTERVAL, 60)

    fun setHydrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HYDRATION_ENABLED, enabled).apply()
    }

    fun isHydrationEnabled(): Boolean = prefs.getBoolean(KEY_HYDRATION_ENABLED, true)

    fun setStepGoal(goal: Int) {
        prefs.edit().putInt(KEY_STEP_GOAL, goal).apply()
    }

    fun getStepGoal(): Int = prefs.getInt(KEY_STEP_GOAL, 10000)

    // Daily steps
    fun setDailySteps(steps: Int) {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        prefs.edit()
            .putInt(KEY_DAILY_STEPS, steps)
            .putString(KEY_LAST_STEP_DATE, today)
            .apply()
    }

    fun getDailySteps(): Int {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            .format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_STEP_DATE, "") ?: ""

        return if (lastDate == today) {
            prefs.getInt(KEY_DAILY_STEPS, 0)
        } else {
            0 // Reset for new day
        }
    }

    fun getCurrentUser(): User? {
        val username = getCurrentUsername() ?: return null
        return User(
            username = username,
            email = prefs.getString(KEY_USER_EMAIL, "") ?: "",
            hasCompletedOnboarding = isOnboardingCompleted(),
            hydrationReminderInterval = getHydrationInterval(),
            isHydrationReminderEnabled = isHydrationEnabled(),
            dailyStepGoal = getStepGoal()
        )
    }
}
