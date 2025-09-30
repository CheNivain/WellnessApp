package com.example.wellnessapp.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.MoodOption

class MoodOptionsAdapter(
    private val moodOptions: List<MoodOption>,
    private val onMoodSelected: (MoodOption) -> Unit
) : RecyclerView.Adapter<MoodOptionsAdapter.MoodOptionViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodOptionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood_option, parent, false)
        return MoodOptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodOptionViewHolder, position: Int) {
        holder.bind(moodOptions[position], position == selectedPosition)
    }

    override fun getItemCount(): Int = moodOptions.size

    inner class MoodOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moodEmoji: TextView = itemView.findViewById(R.id.mood_emoji)
        private val moodName: TextView = itemView.findViewById(R.id.mood_name)

        fun bind(moodOption: MoodOption, isSelected: Boolean) {
            moodEmoji.text = moodOption.emoji
            moodName.text = moodOption.name
            
            if (isSelected) {
                itemView.setBackgroundResource(R.drawable.mood_option_selected_background)
            } else {
                itemView.setBackgroundResource(R.drawable.mood_option_background)
            }
            
            itemView.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                notifyItemChanged(previousPosition)
                notifyItemChanged(selectedPosition)
                onMoodSelected(moodOption)
            }
        }
    }
}