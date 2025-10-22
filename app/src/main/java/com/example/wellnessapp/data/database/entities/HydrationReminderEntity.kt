package com.example.wellnessapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_reminders")
data class HydrationReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val intervalMinutes: Int,
    val lastReminderTime: String
)
