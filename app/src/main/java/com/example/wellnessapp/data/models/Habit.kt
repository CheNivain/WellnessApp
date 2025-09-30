package com.example.wellnessapp.data.models

import java.util.*

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val createdDate: Long = System.currentTimeMillis(),
    val completedDates: MutableSet<String> = mutableSetOf() // Stores dates as "yyyy-MM-dd"
) {
    fun isCompletedToday(): Boolean {
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date())
        return completedDates.contains(today)
    }
    
    fun getTodayCompletionPercentage(totalHabits: Int): Float {
        return if (totalHabits == 0) 0f else {
            val completedToday = if (isCompletedToday()) 1 else 0
            (completedToday.toFloat() / totalHabits.toFloat()) * 100f
        }
    }
}