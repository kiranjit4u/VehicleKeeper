@file:OptIn(ExperimentalMaterial3Api::class)

package com.kiranjit.vehiclekeeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.kiranjit.vehiclekeeper.VehicleViewModel
import com.kiranjit.vehiclekeeper.data.PressureCheck
import java.time.LocalDate

@Composable
fun DetailScreen(vm: VehicleViewModel, id: Long, onBack: () -> Unit, onEdit: (Long) -> Unit) {
    val vehicle by remember(id) { vm.vehicle(id) }.collectAsState(initial = null)
    val checks by remember(id) { vm.pressure(id) }.collectAsState(initial = emptyList())
    var showAdd by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    val v = vehicle

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(v?.displayName() ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(id) }) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    IconButton(onClick = { showDelete = true }) { Icon(Icons.Default.Delete, contentDescription = "Delete") }
                }
            )
        }
    ) { pad ->
        if (v == null) return@Scaffold

        LazyColumn(
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Section("Expiry dates") {
                    ExpiryRow("PUC", v.pucExpiry)
                    ExpiryRow("Insurance", v.insuranceExpiry)
                    ExpiryRow("Road tax", v.roadTaxExpiry)
                }
            }
            item {
                Section("Vehicle details") {
                    DetailRow("Type", v.type)
                    DetailRow("Registration no.", v.registrationNo)
                    DetailRow("Chassis no.", v.chassisNo)
                    DetailRow("Engine no.", v.engineNo)
                    DetailRow("Make", v.make)
                    DetailRow("Model", v.model)
                    DetailRow("Fuel", v.fuelType)
                    DetailRow("Owner", v.ownerName)
                }
            }
            item {
                Section("Insurance") {
                    DetailRow("Provider", v.insuranceProvider)
                    DetailRow("Policy no.", v.policyNo)
                }
            }
            if (v.notes.isNotBlank()) {
                item { Section("Notes") { Text(v.notes) } }
            }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Tyre pressure checks", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { showAdd = true }) { Text("+ Add check") }
                }
            }
            if (checks.isEmpty()) {
                item { Text("No checks logged yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            items(checks, key = { it.id }) { c ->
                Card(Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(fmtDate(c.dateEpochDay), style = MaterialTheme.typography.titleSmall)
                            Text(
                                "Front: ${c.frontPsi?.let { "$it psi" } ?: "-"}   Rear: ${c.rearPsi?.let { "$it psi" } ?: "-"}"
                            )
                            if (c.notes.isNotBlank()) Text(c.notes, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { vm.deletePressure(c) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete check")
                        }
                    }
                }
            }
        }

        if (showAdd) {
            AddPressureDialog(
                onDismiss = { showAdd = false },
                onSave = { date, front, rear, notes ->
                    vm.addPressure(PressureCheck(vehicleId = id, dateEpochDay = date, frontPsi = front, rearPsi = rear, notes = notes))
                    showAdd = false
                }
            )
        }
        if (showDelete) {
            AlertDialog(
                onDismissRequest = { showDelete = false },
                title = { Text("Delete vehicle?") },
                text = { Text("This removes the vehicle and its pressure history.") },
                confirmButton = {
                    TextButton(onClick = { vm.delete(v); showDelete = false; onBack() }) { Text("Delete") }
                },
                dismissButton = { TextButton(onClick = { showDelete = false }) { Text("Cancel") } }
            )
        }
    }
}

@Composable
private fun AddPressureDialog(onDismiss: () -> Unit, onSave: (Long, Double?, Double?, String) -> Unit) {
    var date by remember { mutableStateOf<Long?>(LocalDate.now().toEpochDay()) }
    var front by remember { mutableStateOf("") }
    var rear by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    val num = KeyboardOptions(keyboardType = KeyboardType.Decimal)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log pressure check") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                DateField("Date", date) { date = it ?: date }
                OutlinedTextField(front, { front = it }, label = { Text("Front (psi)") }, keyboardOptions = num, singleLine = true)
                OutlinedTextField(rear, { rear = it }, label = { Text("Rear (psi)") }, keyboardOptions = num, singleLine = true)
                OutlinedTextField(notes, { notes = it }, label = { Text("Notes") })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(date ?: LocalDate.now().toEpochDay(), front.toDoubleOrNull(), rear.toDoubleOrNull(), notes.trim())
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
