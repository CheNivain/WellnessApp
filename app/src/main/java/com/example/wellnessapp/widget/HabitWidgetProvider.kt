package com.example.wellnessapp.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.wellnessapp.MainActivity
import com.example.wellnessapp.R
import com.example.wellnessapp.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        // Use coroutine to fetch data from database
        CoroutineScope(Dispatchers.IO).launch {
            val database = AppDatabase.getDatabase(context)
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayHabits = database.habitDao().getHabitsByDate(today)
            
            // Calculate today's progress
            val completedToday = todayHabits.count { it.isCompleted }
            // Get unique habits (group by name and createdDate)
            val allHabits = database.habitDao().getAllHabits()
            val uniqueHabits = allHabits.distinctBy { "${it.name}_${it.createdDate}" }
            val totalHabits = uniqueHabits.size
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
            
            // Update the widget on main thread
            CoroutineScope(Dispatchers.Main).launch {
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
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