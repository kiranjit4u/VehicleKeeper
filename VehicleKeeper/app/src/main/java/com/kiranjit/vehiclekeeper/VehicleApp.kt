package com.kiranjit.vehiclekeeper

import android.app.Application
import com.kiranjit.vehiclekeeper.work.Reminders

class VehicleApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Reminders.schedule(this)
    }
}
