package com.example.wellnessapp.ui.habits

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.Habit
import com.example.wellnessapp.data.preferences.PreferencesManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {
    
    private lateinit var preferencesManager: PreferencesManager
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
        
        preferencesManager = PreferencesManager(requireContext())
        
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
        habitsAdapter = HabitsAdapter(habits) { habit, completed ->
            toggleHabitCompletion(habit, completed)
        }
        habitsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        habitsRecyclerView.adapter = habitsAdapter
    }
    
    private fun setupClickListeners() {
        addHabitButton.setOnClickListener {
            showAddHabitDialog()
        }
    }
    
    private fun loadHabits() {
        habits.clear()
        habits.addAll(preferencesManager.getHabits())
        habitsAdapter.notifyDataSetChanged()
        updateProgress()
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
        
        if (completed) {
            habit.completedDates.add(today)
        } else {
            habit.completedDates.remove(today)
        }
        
        preferencesManager.updateHabit(habit)
        updateProgress()
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddHabitDialogFragment { habit ->
            preferencesManager.addHabit(habit)
            loadHabits()
        }
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
}