package com.example.wellnessapp.data.models

data class MoodEntry(
    val id: String = java.util.UUID.randomUUID().toString(),
    val emoji: String,
    val moodName: String,
    val moodValue: Int, // 1-10 scale for chart data
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        val MOOD_OPTIONS = listOf(
            MoodOption("😢", "Very Sad", 1),
            MoodOption("😞", "Sad", 2),
            MoodOption("😐", "Neutral", 3),
            MoodOption("🙂", "Okay", 4),
            MoodOption("😊", "Good", 5),
            MoodOption("😄", "Happy", 6),
            MoodOption("🤗", "Great", 7),
            MoodOption("😍", "Amazing", 8),
            MoodOption("🥳", "Fantastic", 9),
            MoodOption("🌟", "Incredible", 10)
        )
    }
}

data class MoodOption(
    val emoji: String,
    val name: String,
    val value: Int
)