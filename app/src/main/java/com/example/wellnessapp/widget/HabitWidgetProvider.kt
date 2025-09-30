package com.example.wellnessapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.wellnessapp.MainActivity
import com.example.wellnessapp.R
import com.example.wellnessapp.data.preferences.PreferencesManager
import java.text.SimpleDateFormat
import java.util.*

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val preferencesManager = PreferencesManager(context)
        val habits = preferencesManager.getHabits()
        
        // Calculate today's progress
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val completedToday = habits.count { it.completedDates.contains(today) }
        val totalHabits = habits.size
        val progressPercentage = if (totalHabits == 0) 0 else (completedToday * 100) / totalHabits
        
        // Create RemoteViews for the widget layout
        val views = RemoteViews(context.packageName, R.layout.habit_widget_layout)
        
        // Update widget content
        views.setTextViewText(R.id.widget_title, "Daily Progress")
        views.setTextViewText(R.id.widget_progress_text, "$progressPercentage%")
        views.setTextViewText(R.id.widget_habits_text, "$completedToday of $totalHabits habits")
        views.setProgressBar(R.id.widget_progress_bar, 100, progressPercentage, false)
        
        // Set up click to open app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
        
        // Update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
    
    companion object {
        fun updateWidget(context: Context) {
            val intent = Intent(context, HabitWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            context.sendBroadcast(intent)
        }
    }
}