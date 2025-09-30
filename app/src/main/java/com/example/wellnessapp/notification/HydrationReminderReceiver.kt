package com.example.wellnessapp.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class HydrationReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        HydrationReminderManager.scheduleReminder(context)
    }
}