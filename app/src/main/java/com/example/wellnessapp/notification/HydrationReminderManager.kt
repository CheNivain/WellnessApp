package com.example.wellnessapp.notification

import android.content.Context
import androidx.work.*
import com.example.wellnessapp.data.preferences.PreferencesManager
import java.util.concurrent.TimeUnit

object HydrationReminderManager {
    
    private const val WORK_NAME = "hydration_reminder_work"
    
    fun scheduleReminder(context: Context) {
        val preferencesManager = PreferencesManager(context)
        
        if (!preferencesManager.isHydrationEnabled()) {
            cancelReminder(context)
            return
        }
        
        val intervalMinutes = preferencesManager.getHydrationInterval().toLong()
        
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(false)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
            intervalMinutes, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
            .build()
        
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest
            )
    }
    
    fun cancelReminder(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
    
    fun updateReminderInterval(context: Context) {
        // Cancel existing and reschedule with new interval
        scheduleReminder(context)
    }
}