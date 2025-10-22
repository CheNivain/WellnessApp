package com.example.wellnessapp.data.database.dao

import androidx.room.*
import com.example.wellnessapp.data.database.entities.HydrationReminderEntity

@Dao
interface HydrationDao {
    @Insert
    suspend fun insert(reminder: HydrationReminderEntity): Long
    
    @Update
    suspend fun update(reminder: HydrationReminderEntity)
    
    @Delete
    suspend fun delete(reminder: HydrationReminderEntity)
    
    @Query("SELECT * FROM hydration_reminders ORDER BY id DESC LIMIT 1")
    suspend fun getLatestReminder(): HydrationReminderEntity?
    
    @Query("SELECT * FROM hydration_reminders")
    suspend fun getAllReminders(): List<HydrationReminderEntity>
}
