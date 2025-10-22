package com.example.wellnessapp.data.database

import android.content.Context
import com.example.wellnessapp.data.database.entities.HabitEntity
import com.example.wellnessapp.data.database.entities.MoodEntity
import com.example.wellnessapp.data.models.Habit
import com.example.wellnessapp.data.models.MoodEntry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Helper class to migrate data from SharedPreferences to Room database
 */
object DataMigrationHelper {
    
    private const val PREFS_NAME = "wellness_app_prefs"
    private const val KEY_HABITS = "habits"
    private const val KEY_MOOD_ENTRIES = "mood_entries"
    private const val KEY_MIGRATION_COMPLETED = "migration_completed"
    
    suspend fun migrateDataIfNeeded(context: Context) = withContext(Dispatchers.IO) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        
        // Check if migration already completed
        if (prefs.getBoolean(KEY_MIGRATION_COMPLETED, false)) {
            return@withContext
        }
        
        val database = AppDatabase.getDatabase(context)
        val gson = Gson()
        
        // Migrate habits
        val habitsJson = prefs.getString(KEY_HABITS, null)
        if (habitsJson != null) {
            try {
                val type = object : TypeToken<List<Habit>>() {}.type
                val habits: List<Habit> = gson.fromJson(habitsJson, type)
                
                for (habit in habits) {
                    // Insert the base habit (with no completion date)
                    database.habitDao().insert(
                        HabitEntity(
                            name = habit.name,
                            description = habit.description,
                            isCompleted = false,
                            date = "",
                            createdDate = habit.createdDate
                        )
                    )
                    
                    // Insert completion records for each date
                    for (date in habit.completedDates) {
                        database.habitDao().insert(
                            HabitEntity(
                                name = habit.name,
                                description = habit.description,
                                isCompleted = true,
                                date = date,
                                createdDate = habit.createdDate
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Migrate mood entries
        val moodsJson = prefs.getString(KEY_MOOD_ENTRIES, null)
        if (moodsJson != null) {
            try {
                val type = object : TypeToken<List<MoodEntry>>() {}.type
                val moods: List<MoodEntry> = gson.fromJson(moodsJson, type)
                
                for (mood in moods) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(Date(mood.timestamp))
                    database.moodDao().insert(
                        MoodEntity(
                            emoji = mood.emoji,
                            description = mood.moodName,
                            moodValue = mood.moodValue,
                            date = date,
                            timestamp = mood.timestamp
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        // Mark migration as completed
        prefs.edit().putBoolean(KEY_MIGRATION_COMPLETED, true).apply()
    }
}
