# Room Database Implementation

## Overview
This document describes the Room database integration for Lab Exam 4 of IT2010 – Mobile Application Development.

## Architecture

### Database Structure
- **Database Name**: `wellness_database`
- **Version**: 1
- **Entities**: 3 (HabitEntity, MoodEntity, HydrationReminderEntity)

### Entities

#### HabitEntity
```kotlin
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val isCompleted: Boolean,
    val date: String,
    val createdDate: Long
)
```

#### MoodEntity
```kotlin
@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val emoji: String,
    val description: String,
    val moodValue: Int,
    val date: String,
    val timestamp: Long
)
```

#### HydrationReminderEntity
```kotlin
@Entity(tableName = "hydration_reminders")
data class HydrationReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val intervalMinutes: Int,
    val lastReminderTime: String
)
```

### DAOs

#### HabitDao
- `insert(habit: HabitEntity): Long` - Insert new habit
- `update(habit: HabitEntity)` - Update existing habit
- `delete(habit: HabitEntity)` - Delete habit
- `getHabitsByDate(date: String): List<HabitEntity>` - Get habits for specific date
- `getAllHabits(): List<HabitEntity>` - Get all habits
- `getHabitById(id: Int): HabitEntity?` - Get habit by ID
- `deleteById(id: Int)` - Delete habit by ID

#### MoodDao
- `insert(mood: MoodEntity): Long` - Insert new mood
- `delete(mood: MoodEntity)` - Delete mood
- `getAllMoods(): List<MoodEntity>` - Get all moods (sorted by timestamp DESC)
- `getMoodByDate(date: String): List<MoodEntity>` - Get moods for specific date
- `getMoodById(id: Int): MoodEntity?` - Get mood by ID
- `deleteById(id: Int)` - Delete mood by ID

#### HydrationDao
- `insert(reminder: HydrationReminderEntity): Long` - Insert new reminder
- `update(reminder: HydrationReminderEntity)` - Update reminder
- `delete(reminder: HydrationReminderEntity)` - Delete reminder
- `getLatestReminder(): HydrationReminderEntity?` - Get latest reminder
- `getAllReminders(): List<HydrationReminderEntity>` - Get all reminders

### Database Singleton
The `AppDatabase` class implements the singleton pattern:
```kotlin
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
```

## Migration from SharedPreferences

### DataMigrationHelper
A utility class (`DataMigrationHelper`) automatically migrates existing data from SharedPreferences to Room database on first app launch after the update.

**Migration Process:**
1. Checks if migration has already been completed
2. Reads habit and mood data from SharedPreferences
3. Converts and inserts data into Room database
4. Marks migration as completed to prevent duplicate migrations

**Execution:**
The migration is triggered in `MainActivity.onCreate()`:
```kotlin
lifecycleScope.launch {
    DataMigrationHelper.migrateDataIfNeeded(this@MainActivity)
}
```

## Usage in Fragments

### Using Coroutines
All DAO methods are `suspend` functions and must be called within a coroutine scope:

```kotlin
// In HabitsFragment
lifecycleScope.launch {
    val habits = database.habitDao().getAllHabits()
    // Update UI
}
```

### Example: Adding a Habit
```kotlin
lifecycleScope.launch {
    database.habitDao().insert(
        HabitEntity(
            name = "Drink Water",
            description = "Stay hydrated",
            isCompleted = false,
            date = "",
            createdDate = System.currentTimeMillis()
        )
    )
    loadHabits() // Refresh UI
}
```

### Example: Adding a Mood
```kotlin
lifecycleScope.launch {
    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    database.moodDao().insert(
        MoodEntity(
            emoji = "😊",
            description = "Happy",
            moodValue = 8,
            date = date,
            timestamp = System.currentTimeMillis()
        )
    )
    loadMoodEntries() // Refresh UI
}
```

## SharedPreferences Retention

The following data remains in SharedPreferences (lightweight settings):
- User authentication (login status, credentials)
- Onboarding completion status
- Hydration reminder settings (interval, enabled/disabled)
- Step goal
- Daily step count
- Daily water intake (resets each day)

## Files Modified

### Core Database Files (New)
- `data/database/AppDatabase.kt` - Room database singleton
- `data/database/entities/HabitEntity.kt` - Habit entity
- `data/database/entities/MoodEntity.kt` - Mood entity
- `data/database/entities/HydrationReminderEntity.kt` - Hydration reminder entity
- `data/database/dao/HabitDao.kt` - Habit data access object
- `data/database/dao/MoodDao.kt` - Mood data access object
- `data/database/dao/HydrationDao.kt` - Hydration data access object
- `data/database/DataMigrationHelper.kt` - Migration utility

### Updated Files
- `app/build.gradle` - Added Room dependencies and kapt plugin
- `data/preferences/PreferencesManager.kt` - Removed habit/mood methods, kept only settings
- `ui/habits/HabitsFragment.kt` - Migrated to Room DAOs
- `ui/mood/MoodFragment.kt` - Migrated to Room DAOs
- `MainActivity.kt` - Added migration trigger and Room usage
- `widget/HabitWidgetProvider.kt` - Updated to fetch data from Room

## Testing Data Persistence

To verify data persists across app restarts:
1. Add some habits and mood entries
2. Close the app completely (swipe from recent apps)
3. Reopen the app
4. Verify all data is still present

## Dependencies Added

```gradle
def room_version = "2.6.1"
implementation "androidx.room:room-runtime:$room_version"
kapt "androidx.room:room-compiler:$room_version"
implementation "androidx.room:room-ktx:$room_version"
```

## Future Enhancements

Potential improvements for future lab exams:
- Add database versioning and migration strategies
- Implement LiveData or Flow for reactive database updates
- Add Repository pattern for better separation of concerns
- Implement database export/import functionality
- Add more comprehensive error handling
