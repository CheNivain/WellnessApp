package com.example.wellnessapp.ui.mood

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.MoodEntry
import com.example.wellnessapp.data.database.AppDatabase
import com.example.wellnessapp.data.database.entities.MoodEntity
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MoodFragment : Fragment() {
    
    private lateinit var database: AppDatabase
    private lateinit var moodRecyclerView: RecyclerView
    private lateinit var addMoodFab: FloatingActionButton
    private lateinit var shareMoodButton: MaterialButton
    private lateinit var viewTrendsButton: MaterialButton
    private lateinit var moodAdapter: MoodAdapter
    
    private var moodEntries = mutableListOf<MoodEntry>()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mood, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        database = AppDatabase.getDatabase(requireContext())
        
        initViews(view)
        setupRecyclerView()
        setupClickListeners()
        loadMoodEntries()
    }
    
    override fun onResume() {
        super.onResume()
        loadMoodEntries()
    }
    
    private fun initViews(view: View) {
        moodRecyclerView = view.findViewById(R.id.mood_recycler_view)
        addMoodFab = view.findViewById(R.id.add_mood_fab)
        shareMoodButton = view.findViewById(R.id.share_mood_button)
        viewTrendsButton = view.findViewById(R.id.view_trends_button)
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(moodEntries) { moodEntry ->
            showDeleteConfirmation(moodEntry)
        }
        
        // Use GridLayoutManager for tablets (sw600dp and above)
        val spanCount = resources.getInteger(R.integer.mood_grid_columns)
        moodRecyclerView.layoutManager = if (spanCount > 1) {
            androidx.recyclerview.widget.GridLayoutManager(requireContext(), spanCount)
        } else {
            LinearLayoutManager(requireContext())
        }
        moodRecyclerView.adapter = moodAdapter
    }
    
    private fun setupClickListeners() {
        addMoodFab.setOnClickListener {
            showAddMoodDialog()
        }
        
        shareMoodButton.setOnClickListener {
            shareMoodSummary()
        }
        
        viewTrendsButton.setOnClickListener {
            showMoodTrends()
        }
    }
    
    private fun loadMoodEntries() {
        lifecycleScope.launch {
            val moodEntities = database.moodDao().getAllMoods()
            moodEntries.clear()
            moodEntries.addAll(moodEntities.map { entity ->
                MoodEntry(
                    id = entity.id.toString(),
                    emoji = entity.emoji,
                    moodName = entity.description,
                    moodValue = entity.moodValue,
                    note = "",
                    timestamp = entity.timestamp
                )
            })
            moodAdapter.notifyDataSetChanged()
        }
    }
    
    private fun showAddMoodDialog() {
        val dialog = AddMoodDialogFragment { moodEntry ->
            lifecycleScope.launch {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(moodEntry.timestamp))
                database.moodDao().insert(
                    MoodEntity(
                        emoji = moodEntry.emoji,
                        description = moodEntry.moodName,
                        moodValue = moodEntry.moodValue,
                        date = date,
                        timestamp = moodEntry.timestamp
                    )
                )
                loadMoodEntries()
            }
        }
        dialog.show(parentFragmentManager, "AddMoodDialog")
    }
    
    private fun shareMoodSummary() {
        val recentMoods = moodEntries.take(7) // Last 7 entries
        if (recentMoods.isEmpty()) {
            return
        }
        
        val avgMood = recentMoods.map { it.moodValue }.average()
        val summary = buildString {
            appendLine("My Wellness Mood Summary 😊")
            appendLine("Average mood (last 7 entries): ${String.format("%.1f", avgMood)}/10")
            appendLine("Recent moods:")
            recentMoods.take(5).forEach { mood ->
                val date = java.text.SimpleDateFormat("MMM dd", java.util.Locale.getDefault())
                    .format(java.util.Date(mood.timestamp))
                appendLine("${mood.emoji} ${mood.moodName} - $date")
            }
            appendLine("\nShared from Wellness App")
        }
        
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, summary)
            putExtra(Intent.EXTRA_SUBJECT, "My Mood Summary")
        }
        
        startActivity(Intent.createChooser(shareIntent, "Share Mood Summary"))
    }
    
    private fun showMoodTrends() {
        val dialog = MoodTrendsDialogFragment(moodEntries)
        dialog.show(parentFragmentManager, "MoodTrendsDialog")
    }
    
    private fun showDeleteConfirmation(moodEntry: MoodEntry) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Delete Mood Entry")
            .setMessage("Are you sure you want to delete this mood entry?")
            .setPositiveButton("Delete") { _, _ ->
                lifecycleScope.launch {
                    database.moodDao().deleteById(moodEntry.id.toInt())
                    loadMoodEntries()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}