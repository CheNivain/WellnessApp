package com.example.wellnessapp.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.MoodEntry
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private val moodEntries: List<MoodEntry>,
    private val onDeleteMood: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(moodEntries[position])
    }

    override fun getItemCount(): Int = moodEntries.size

    inner class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moodEmoji: TextView = itemView.findViewById(R.id.mood_emoji)
        private val moodName: TextView = itemView.findViewById(R.id.mood_name)
        private val moodNote: TextView = itemView.findViewById(R.id.mood_note)
        private val moodDate: TextView = itemView.findViewById(R.id.mood_date)
        private val moodTime: TextView = itemView.findViewById(R.id.mood_time)
        private val deleteButton: MaterialButton = itemView.findViewById(R.id.delete_mood_button)

        fun bind(moodEntry: MoodEntry) {
            moodEmoji.text = moodEntry.emoji
            moodName.text = moodEntry.moodName
            
            if (moodEntry.note.isNotEmpty()) {
                moodNote.text = moodEntry.note
                moodNote.visibility = View.VISIBLE
            } else {
                moodNote.visibility = View.GONE
            }
            
            val date = Date(moodEntry.timestamp)
            val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            moodDate.text = dateFormat.format(date)
            moodTime.text = timeFormat.format(date)
            
            deleteButton.setOnClickListener {
                onDeleteMood(moodEntry)
            }
        }
    }
}