@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.kiranjit.vehiclekeeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.kiranjit.vehiclekeeper.VehicleViewModel
import com.kiranjit.vehiclekeeper.data.Vehicle

private val vehicleTypes = listOf("Car", "Bike", "Scooter", "Truck", "Other")
private val fuelTypes = listOf("Petrol", "Diesel", "CNG", "Electric", "Other")

@Composable
fun EditScreen(vm: VehicleViewModel, id: Long, onDone: () -> Unit) {
    val existing by remember(id) { vm.vehicle(id) }.collectAsState(initial = null)
    var loaded by remember { mutableStateOf(id == 0L) }

    var nickname by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Car") }
    var reg by remember { mutableStateOf("") }
    var chassis by remember { mutableStateOf("") }
    var engine by remember { mutableStateOf("") }
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var fuel by remember { mutableStateOf("Petrol") }
    var owner by remember { mutableStateOf("") }
    var puc by remember { mutableStateOf<Long?>(null) }
    var insProvider by remember { mutableStateOf("") }
    var policyNo by remember { mutableStateOf("") }
    var insExpiry by remember { mutableStateOf<Long?>(null) }
    var taxExpiry by remember { mutableStateOf<Long?>(null) }
    var notes by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(existing) {
        val e = existing
        if (!loaded && e != null) {
            nickname = e.nickname; type = e.type; reg = e.registrationNo; chassis = e.chassisNo
            engine = e.engineNo; make = e.make; model = e.model; fuel = e.fuelType; owner = e.ownerName
            puc = e.pucExpiry; insProvider = e.insuranceProvider; policyNo = e.policyNo
            insExpiry = e.insuranceExpiry; taxExpiry = e.roadTaxExpiry; notes = e.notes
            loaded = true
        }
    }

    val caps = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (id == 0L) "Add vehicle" else "Edit vehicle") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Vehicle", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(nickname, { nickname = it }, label = { Text("Nickname (optional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Chips(vehicleTypes, type) { type = it }
            OutlinedTextField(
                reg, { reg = it.uppercase() }, label = { Text("Registration number *") },
                isError = showError && reg.isBlank(), modifier = Modifier.fillMaxWidth(),
                singleLine = true, keyboardOptions = caps
            )
            OutlinedTextField(chassis, { chassis = it.uppercase() }, label = { Text("Chassis number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = caps)
            OutlinedTextField(engine, { engine = it.uppercase() }, label = { Text("Engine number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = caps)
            OutlinedTextField(make, { make = it }, label = { Text("Make") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(model, { model = it }, label = { Text("Model") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Chips(fuelTypes, fuel) { fuel = it }
            OutlinedTextField(owner, { owner = it }, label = { Text("Owner name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

            Text("PUC", style = MaterialTheme.typography.titleMedium)
            DateField("PUC expiry", puc) { puc = it }

            Text("Insurance", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(insProvider, { insProvider = it }, label = { Text("Insurance provider") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(policyNo, { policyNo = it.uppercase() }, label = { Text("Policy number") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = caps)
            DateField("Insurance expiry", insExpiry) { insExpiry = it }

            Text("Other", style = MaterialTheme.typography.titleMedium)
            DateField("Road tax expiry", taxExpiry) { taxExpiry = it }
            OutlinedTextField(notes, { notes = it }, label = { Text("Notes") }, modifier = Modifier.fillMaxWidth())

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (reg.isBlank()) {
                        showError = true
                    } else {
                        vm.save(
                            Vehicle(
                                id = id, nickname = nickname.trim(), type = type, registrationNo = reg.trim(),
                                chassisNo = chassis.trim(), engineNo = engine.trim(), make = make.trim(),
                                model = model.trim(), fuelType = fuel, ownerName = owner.trim(),
                                pucExpiry = puc, insuranceProvider = insProvider.trim(), policyNo = policyNo.trim(),
                                insuranceExpiry = insExpiry, roadTaxExpiry = taxExpiry, notes = notes.trim()
                            ),
                            onDone
                        )
                    }
                }
            ) { Text("Save") }
        }
    }
}

@Composable
private fun Chips(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { o ->
            FilterChip(selected = selected == o, onClick = { onSelect(o) }, label = { Text(o) })
        }
    }
}
