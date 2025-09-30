package com.example.wellnessapp.ui.mood

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.MoodEntry
import com.example.wellnessapp.data.models.MoodOption
import com.google.android.material.textfield.TextInputEditText

class AddMoodDialogFragment(
    private val onMoodAdded: (MoodEntry) -> Unit
) : DialogFragment() {

    private var selectedMoodOption: MoodOption? = null
    private lateinit var noteEditText: TextInputEditText
    private lateinit var moodOptionsAdapter: MoodOptionsAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_mood, null)
        
        val moodRecyclerView = view.findViewById<RecyclerView>(R.id.mood_options_recycler_view)
        noteEditText = view.findViewById(R.id.mood_note_edit_text)
        
        setupMoodOptions(moodRecyclerView)
        
        return AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->
                saveMood()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }
    
    private fun setupMoodOptions(recyclerView: RecyclerView) {
        moodOptionsAdapter = MoodOptionsAdapter(MoodEntry.MOOD_OPTIONS) { moodOption ->
            selectedMoodOption = moodOption
        }
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerView.adapter = moodOptionsAdapter
    }
    
    private fun saveMood() {
        val selected = selectedMoodOption
        if (selected != null) {
            val note = noteEditText.text.toString().trim()
            val moodEntry = MoodEntry(
                emoji = selected.emoji,
                moodName = selected.name,
                moodValue = selected.value,
                note = note
            )
            onMoodAdded(moodEntry)
        } else {
            Toast.makeText(requireContext(), "Please select a mood", Toast.LENGTH_SHORT).show()
        }
    }
}