package com.kiranjit.vehiclekeeper.work

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kiranjit.vehiclekeeper.MainActivity
import com.kiranjit.vehiclekeeper.data.Vehicle

/** Notify when a document expires within this many days (or is already expired). */
const val REMINDER_DAYS = 5L

object Notifier {
    private const val CHANNEL_ID = "expiry"

    private fun ensureChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            nm.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Document expiry", NotificationManager.IMPORTANCE_HIGH)
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun notifyExpiry(ctx: Context, vehicle: Vehicle, label: String, daysLeft: Long, notificationId: Int) {
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return

        ensureChannel(ctx)

        val text = when {
            daysLeft < 0 -> "$label expired ${-daysLeft} day(s) ago"
            daysLeft == 0L -> "$label expires today"
            else -> "$label expires in $daysLeft day(s)"
        }

        val intent = Intent(ctx, MainActivity::class.java)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pi = PendingIntent.getActivity(
            ctx, notificationId, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val n = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle("${vehicle.displayName()} - $label")
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(ctx).notify(notificationId, n)
    }
}
