package com.kiranjit.vehiclekeeper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Query("SELECT * FROM vehicles ORDER BY nickname COLLATE NOCASE, registrationNo")
    fun observeAll(): Flow<List<Vehicle>>

    @Query("SELECT * FROM vehicles")
    suspend fun getAll(): List<Vehicle>

    @Query("SELECT * FROM vehicles WHERE id = :id")
    fun observe(id: Long): Flow<Vehicle?>

    @Upsert
    suspend fun upsert(vehicle: Vehicle): Long

    @Delete
    suspend fun delete(vehicle: Vehicle)

    @Query("SELECT * FROM pressure_checks WHERE vehicleId = :vehicleId ORDER BY dateEpochDay DESC, id DESC")
    fun observePressure(vehicleId: Long): Flow<List<PressureCheck>>

    @Insert
    suspend fun insertPressure(check: PressureCheck)

    @Delete
    suspend fun deletePressure(check: PressureCheck)
}
