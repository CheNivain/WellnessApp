package com.example.wellnessapp.ui.habits

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.Habit
import com.google.android.material.checkbox.MaterialCheckBox
import java.text.SimpleDateFormat
import java.util.*

class HabitsAdapter(
    private val habits: List<Habit>,
    private val onHabitToggled: (Habit, Boolean) -> Unit
) : RecyclerView.Adapter<HabitsAdapter.HabitViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val habitName: TextView = itemView.findViewById(R.id.habit_name)
        private val habitDescription: TextView = itemView.findViewById(R.id.habit_description)
        private val habitCheckbox: MaterialCheckBox = itemView.findViewById(R.id.habit_checkbox)
        private val createdDate: TextView = itemView.findViewById(R.id.created_date)

        fun bind(habit: Habit) {
            habitName.text = habit.name
            
            if (habit.description.isNotEmpty()) {
                habitDescription.text = habit.description
                habitDescription.visibility = View.VISIBLE
            } else {
                habitDescription.visibility = View.GONE
            }
            
            val createdDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            createdDate.text = "Created: ${createdDateFormat.format(Date(habit.createdDate))}"
            
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val isCompletedToday = habit.completedDates.contains(today)
            
            habitCheckbox.isChecked = isCompletedToday
            
            habitCheckbox.setOnCheckedChangeListener { _, isChecked ->
                onHabitToggled(habit, isChecked)
            }
            
            itemView.setOnClickListener {
                habitCheckbox.isChecked = !habitCheckbox.isChecked
            }
        }
    }
}