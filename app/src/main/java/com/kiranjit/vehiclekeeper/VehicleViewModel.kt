package com.kiranjit.vehiclekeeper

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kiranjit.vehiclekeeper.data.AppDatabase
import com.kiranjit.vehiclekeeper.data.PressureCheck
import com.kiranjit.vehiclekeeper.data.Vehicle
import com.kiranjit.vehiclekeeper.work.Reminders
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).dao()

    val vehicles = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun vehicle(id: Long) = dao.observe(id)
    fun pressure(id: Long) = dao.observePressure(id)

    fun save(vehicle: Vehicle, onDone: () -> Unit) {
        viewModelScope.launch {
            dao.upsert(vehicle)
            onDone()
        }
    }

    fun delete(vehicle: Vehicle) {
        viewModelScope.launch { dao.delete(vehicle) }
    }

    fun addPressure(check: PressureCheck) {
        viewModelScope.launch { dao.insertPressure(check) }
    }

    fun deletePressure(check: PressureCheck) {
        viewModelScope.launch { dao.deletePressure(check) }
    }

    fun checkNow() = Reminders.checkNow(getApplication())
}
