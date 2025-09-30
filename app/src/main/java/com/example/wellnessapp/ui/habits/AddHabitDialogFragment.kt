package com.example.wellnessapp.ui.habits

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.Habit
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddHabitDialogFragment(
    private val onHabitAdded: (Habit) -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_habit, null)
        
        val nameLayout = view.findViewById<TextInputLayout>(R.id.habit_name_layout)
        val nameEditText = view.findViewById<TextInputEditText>(R.id.habit_name_edit_text)
        val descriptionEditText = view.findViewById<TextInputEditText>(R.id.habit_description_edit_text)
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Add New Habit")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                val name = nameEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                
                if (name.isNotEmpty()) {
                    val habit = Habit(
                        name = name,
                        description = description
                    )
                    onHabitAdded(habit)
                } else {
                    Toast.makeText(requireContext(), "Habit name is required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
}