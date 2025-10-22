package com.example.wellnessapp.data.database.dao

import androidx.room.*
import com.example.wellnessapp.data.database.entities.MoodEntity

@Dao
interface MoodDao {
    @Insert
    suspend fun insert(mood: MoodEntity): Long
    
    @Delete
    suspend fun delete(mood: MoodEntity)
    
    @Query("SELECT * FROM moods ORDER BY timestamp DESC")
    suspend fun getAllMoods(): List<MoodEntity>
    
    @Query("SELECT * FROM moods WHERE date = :date ORDER BY timestamp DESC")
    suspend fun getMoodByDate(date: String): List<MoodEntity>
    
    @Query("SELECT * FROM moods WHERE id = :id")
    suspend fun getMoodById(id: Int): MoodEntity?
    
    @Query("DELETE FROM moods WHERE id = :id")
    suspend fun deleteById(id: Int)
}
