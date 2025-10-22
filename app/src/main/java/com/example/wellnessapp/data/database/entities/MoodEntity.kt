package com.example.wellnessapp.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emoji: String,
    val description: String,
    val moodValue: Int,
    val date: String,
    val timestamp: Long
)
