package com.example.wellnessapp.ui.mood

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.wellnessapp.R
import com.example.wellnessapp.data.models.MoodEntry
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.text.SimpleDateFormat
import java.util.*

class MoodTrendsDialogFragment(
    private val moodEntries: List<MoodEntry>
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_mood_trends, null)
        
        val chart = view.findViewById<LineChart>(R.id.mood_chart)
        setupChart(chart)
        
        return AlertDialog.Builder(requireContext())
            .setTitle("Mood Trends (Last 30 Days)")
            .setView(view)
            .setPositiveButton("Close", null)
            .create()
    }
    
    private fun setupChart(chart: LineChart) {
        val entries = mutableListOf<Entry>()
        
        // Get last 30 days of mood data
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        val recentMoods = moodEntries
            .filter { it.timestamp >= thirtyDaysAgo }
            .sortedBy { it.timestamp }
        
        if (recentMoods.isEmpty()) {
            // Show empty state
            chart.clear()
            return
        }
        
        // Create entries for chart
        recentMoods.forEachIndexed { index, mood ->
            entries.add(Entry(index.toFloat(), mood.moodValue.toFloat()))
        }
        
        val dataSet = LineDataSet(entries, "Mood Level").apply {
            color = resources.getColor(R.color.primary_color, null)
            setCircleColor(resources.getColor(R.color.primary_color, null))
            lineWidth = 2f
            circleRadius = 4f
            setDrawFilled(true)
            fillColor = resources.getColor(R.color.primary_color, null)
            fillAlpha = 50
        }
        
        val lineData = LineData(dataSet)
        
        chart.apply {
            data = lineData
            description = Description().apply { text = "" }
            axisLeft.apply {
                axisMinimum = 1f
                axisMaximum = 10f
                granularity = 1f
            }
            axisRight.isEnabled = false
            xAxis.apply {
                granularity = 1f
                setDrawGridLines(false)
            }
            legend.isEnabled = false
            animateX(500)
            invalidate()
        }
    }
}