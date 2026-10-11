package com.kiranjit.vehiclekeeper.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kiranjit.vehiclekeeper.data.AppDatabase
import com.kiranjit.vehiclekeeper.data.Vehicle
import java.time.LocalDate

class ExpiryWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val today = LocalDate.now().toEpochDay()
        val vehicles = AppDatabase.get(applicationContext).dao().getAll()
        for (v in vehicles) {
            check(v, "PUC", v.pucExpiry, today, 1)
            check(v, "Insurance", v.insuranceExpiry, today, 2)
            check(v, "Road tax", v.roadTaxExpiry, today, 3)
        }
        return Result.success()
    }

    private fun check(v: Vehicle, label: String, expiry: Long?, today: Long, slot: Int) {
        if (expiry == null) return
        val daysLeft = expiry - today
        if (daysLeft <= REMINDER_DAYS) {
            Notifier.notifyExpiry(applicationContext, v, label, daysLeft, (v.id * 10 + slot).toInt())
        }
    }
}
