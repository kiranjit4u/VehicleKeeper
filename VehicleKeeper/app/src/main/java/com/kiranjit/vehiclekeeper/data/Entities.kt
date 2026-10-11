package com.kiranjit.vehiclekeeper.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// All dates are stored as LocalDate.toEpochDay() values.
@Entity(tableName = "vehicles")
data class Vehicle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nickname: String = "",
    val type: String = "Car",
    val registrationNo: String = "",
    val chassisNo: String = "",
    val engineNo: String = "",
    val make: String = "",
    val model: String = "",
    val fuelType: String = "Petrol",
    val ownerName: String = "",
    val pucExpiry: Long? = null,
    val insuranceProvider: String = "",
    val policyNo: String = "",
    val insuranceExpiry: Long? = null,
    val roadTaxExpiry: Long? = null,
    val notes: String = ""
) {
    fun displayName(): String = nickname.ifBlank { registrationNo }
}

@Entity(
    tableName = "pressure_checks",
    foreignKeys = [ForeignKey(
        entity = Vehicle::class,
        parentColumns = ["id"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class PressureCheck(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: Long,
    val dateEpochDay: Long,
    val frontPsi: Double?,
    val rearPsi: Double?,
    val notes: String = ""
)
