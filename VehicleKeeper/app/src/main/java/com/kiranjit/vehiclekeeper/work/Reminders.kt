package com.kiranjit.vehiclekeeper.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

object Reminders {
    /** Runs the expiry check once a day at about 9:00 AM. */
    fun schedule(ctx: Context) {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(9, 0)
        if (!next.isAfter(now)) next = next.plusDays(1)
        val delayMinutes = Duration.between(now, next).toMinutes()

        val request = PeriodicWorkRequestBuilder<ExpiryWorker>(1, TimeUnit.DAYS)
            .setInitialDelay(delayMinutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(ctx).enqueueUniquePeriodicWork(
            "expiry_check", ExistingPeriodicWorkPolicy.KEEP, request
        )
    }

    /** Immediate check, handy for testing. */
    fun checkNow(ctx: Context) {
        WorkManager.getInstance(ctx).enqueue(OneTimeWorkRequestBuilder<ExpiryWorker>().build())
    }
}
