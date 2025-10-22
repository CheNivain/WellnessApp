package com.example.wellnessapp.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.Habit
import com.example.wellnessapp.data.database.AppDatabase
import com.example.wellnessapp.data.database.entities.HabitEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.TextView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {
    
    private lateinit var database: AppDatabase
    private lateinit var habitsRecyclerView: RecyclerView
    private lateinit var addHabitButton: MaterialButton
    private lateinit var progressIndicator: CircularProgressIndicator
    private lateinit var progressText: TextView
    private lateinit var completedTodayText: TextView
    private lateinit var habitsAdapter: HabitsAdapter
    
    private var habits = mutableListOf<Habit>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_habits, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AppDatabase.getDatabase(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }
    
    override fun onResume() {
        super.onResume()
        loadHabits() // Refresh data when returning to fragment
    }
    
    private fun initViews(view: View) {
        habitsRecyclerView = view.findViewById(R.id.habits_recycler_view)
        addHabitButton = view.findViewById(R.id.add_habit_button)
        progressIndicator = view.findViewById(R.id.progress_indicator)
        progressText = view.findViewById(R.id.progress_text)
        completedTodayText = view.findViewById(R.id.completed_today_text)
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter(
            habits = habits,
            onHabitToggled = { habit, completed ->
                toggleHabitCompletion(habit, completed)
            },
            onEditHabit = { habit ->
                showEditHabitDialog(habit)
            },
            onDeleteHabit = { habit ->
                showDeleteConfirmation(habit)
            }
        )
        
        // Use GridLayoutManager for tablets (sw600dp and above)
        val spanCount = resources.getInteger(R.integer.habits_grid_columns)
        habitsRecyclerView.layoutManager = if (spanCount > 1) {
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), spanCount)
        } else {
            LinearLayoutManager(requireContext())
        }
        habitsRecyclerView.adapter = habitsAdapter
    }
    
    private fun setupClickListeners() {
        addHabitButton.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        lifecycleScope.launch {
            val habitEntities = database.habitDao().getAllHabits()
            habits.clear()
            // Group habits by name and aggregate completed dates
            val habitMap = mutableMapOf<String, Habit>()
            for (entity in habitEntities) {
                val key = "${entity.name}_${entity.description}_${entity.createdDate}"
                if (!habitMap.containsKey(key)) {
                    habitMap[key] = Habit(
                        id = entity.name + entity.createdDate.toString(),
                        name = entity.name,
                        description = entity.description,
                        createdDate = entity.createdDate,
                        completedDates = mutableSetOf()
                    )
                }
                if (entity.isCompleted) {
                    habitMap[key]?.completedDates?.add(entity.date)
                }
            }
            habits.addAll(habitMap.values)
            habitsAdapter.notifyDataSetChanged()
            updateProgress()
        }
    }
    
    private fun updateProgress() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val completedToday = habits.count { it.completedDates.contains(today) }
        val totalHabits = habits.size
        
        val progressPercentage = if (totalHabits == 0) 0 else (completedToday * 100) / totalHabits
        
        progressIndicator.progress = progressPercentage
        progressText.text = "$progressPercentage%"
        completedTodayText.text = "$completedToday of $totalHabits habits completed today"
    }
    
    private fun toggleHabitCompletion(habit: Habit, completed: Boolean) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        
        lifecycleScope.launch {
            // Find or create habit entity for today
            val existingHabits = database.habitDao().getHabitsByDate(today)
            val existingHabit = existingHabits.find { 
                it.name == habit.name && it.description == habit.description 
            }
            
            if (completed) {
                if (existingHabit == null) {
                    // Insert new habit completion for today
                    database.habitDao().insert(
                        HabitEntity(
                            name = habit.name,
                            description = habit.description,
                            isCompleted = true,
                            date = today,
                            createdDate = habit.createdDate
                        )
                    )
                } else {
                    // Update existing habit
                    database.habitDao().update(existingHabit.copy(isCompleted = true))
                }
                habit.completedDates.add(today)
            } else {
                if (existingHabit != null) {
                    database.habitDao().delete(existingHabit)
                }
                habit.completedDates.remove(today)
            }
            
            updateProgress()
            
            // Update widget
            com.example.wellnessapp.widget.HabitWidgetProvider.updateWidget(requireContext())
        }
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddHabitDialogFragment { habit ->
            lifecycleScope.launch {
                // Insert a new habit entity (not completed initially)
                database.habitDao().insert(
                    HabitEntity(
                        name = habit.name,
                        description = habit.description,
                        isCompleted = false,
                        date = "",
                        createdDate = habit.createdDate
                    )
                )
                loadHabits()
                // Update widget
                com.example.wellnessapp.widget.HabitWidgetProvider.updateWidget(requireContext())
            }
        }
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialog = EditHabitDialogFragment(habit) { updatedHabit ->
            lifecycleScope.launch {
                // Update all habit entities with this name and creation date
                val allHabits = database.habitDao().getAllHabits()
                for (entity in allHabits) {
                    if (entity.name == habit.name && entity.createdDate == habit.createdDate) {
                        database.habitDao().update(
                            entity.copy(
                                name = updatedHabit.name,
                                description = updatedHabit.description
                            )
                        )
                    }
                }
                loadHabits()
                // Update widget
                com.example.wellnessapp.widget.HabitWidgetProvider.updateWidget(requireContext())
            }
        }
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }
    
    private fun showDeleteConfirmation(habit: Habit) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    // Delete all habit entities with this name and creation date
                    val allHabits = database.habitDao().getAllHabits()
                    for (entity in allHabits) {
                        if (entity.name == habit.name && entity.createdDate == habit.createdDate) {
                            database.habitDao().delete(entity)
                        }
                    }
                    loadHabits()
                    // Update widget
                    com.example.wellnessapp.widget.HabitWidgetProvider.updateWidget(requireContext())
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}