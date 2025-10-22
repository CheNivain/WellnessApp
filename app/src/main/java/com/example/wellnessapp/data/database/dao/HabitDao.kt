package com.example.wellnessapp.data.database.dao

import androidx.room.*
import com.example.wellnessapp.data.database.entities.HabitEntity

@Dao
interface HabitDao {
    @Insert
    suspend fun insert(habit: HabitEntity): Long
    
    @Update
    suspend fun update(habit: HabitEntity)
    
    @Delete
    suspend fun delete(habit: HabitEntity)
    
    @Query("SELECT * FROM habits WHERE date = :date ORDER BY createdDate DESC")
    suspend fun getHabitsByDate(date: String): List<HabitEntity>
    
    @Query("SELECT * FROM habits ORDER BY createdDate DESC")
    suspend fun getAllHabits(): List<HabitEntity>
    
    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Int): HabitEntity?
    
    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun deleteById(id: Int)
}
