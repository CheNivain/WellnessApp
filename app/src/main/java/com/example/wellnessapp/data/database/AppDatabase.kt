package com.example.wellnessapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.wellnessapp.data.database.dao.HabitDao
import com.example.wellnessapp.data.database.dao.HydrationDao
import com.example.wellnessapp.data.database.dao.MoodDao
import com.example.wellnessapp.data.database.entities.HabitEntity
import com.example.wellnessapp.data.database.entities.HydrationReminderEntity
import com.example.wellnessapp.data.database.entities.MoodEntity

@Database(
    entities = [HabitEntity::class, MoodEntity::class, HydrationReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun habitDao(): HabitDao
    abstract fun moodDao(): MoodDao
    abstract fun hydrationDao(): HydrationDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "wellness_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
