@file:OptIn(ExperimentalMaterial3Api::class)

package com.kiranjit.vehiclekeeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kiranjit.vehiclekeeper.VehicleViewModel

@Composable
fun VehicleListScreen(vm: VehicleViewModel, onAdd: () -> Unit, onOpen: (Long) -> Unit) {
    val vehicles by vm.vehicles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Vehicles") },
                actions = {
                    IconButton(onClick = { vm.checkNow() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Check reminders now")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add vehicle")
            }
        }
    ) { pad ->
        if (vehicles.isEmpty()) {
            Box(Modifier.padding(pad).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No vehicles yet. Tap + to add one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vehicles, key = { it.id }) { v ->
                    Card(onClick = { onOpen(v.id) }, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(v.displayName(), style = MaterialTheme.typography.titleMedium)
                            Text(
                                listOf(v.registrationNo, v.make, v.model).filter { it.isNotBlank() }.joinToString(" - "),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            ExpiryRow("PUC", v.pucExpiry)
                            ExpiryRow("Insurance", v.insuranceExpiry)
                        }
                    }
                }
            }
        }
    }
}
