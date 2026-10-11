@file:OptIn(ExperimentalMaterial3Api::class)

package com.kiranjit.vehiclekeeper.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kiranjit.vehiclekeeper.work.REMINDER_DAYS
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy")

fun fmtDate(epochDay: Long): String = LocalDate.ofEpochDay(epochDay).format(dateFmt)

/** Returns display text and whether it should be highlighted as urgent. */
fun expiryText(epochDay: Long?): Pair<String, Boolean> {
    if (epochDay == null) return "Not set" to false
    val left = epochDay - LocalDate.now().toEpochDay()
    val d = fmtDate(epochDay)
    return when {
        left < 0 -> "$d (expired)" to true
        left == 0L -> "$d (today)" to true
        left <= REMINDER_DAYS -> "$d ($left days left)" to true
        else -> d to false
    }
}

@Composable
fun ExpiryRow(label: String, epochDay: Long?) {
    val (text, urgent) = expiryText(epochDay)
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label)
        Text(
            text,
            color = if (urgent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (urgent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    if (value.isBlank()) return
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value)
    }
}

@Composable
fun Section(title: String, content: @Composable () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
fun DateField(label: String, epochDay: Long?, onChange: (Long?) -> Unit) {
    var show by remember { mutableStateOf(false) }
    OutlinedButton(onClick = { show = true }, modifier = Modifier.fillMaxWidth()) {
        Text(if (epochDay == null) "$label: Not set" else "$label: ${fmtDate(epochDay)}")
    }
    if (show) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = epochDay?.let {
                LocalDate.ofEpochDay(it).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            }
        )
        DatePickerDialog(
            onDismissRequest = { show = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let {
                        onChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay())
                    }
                    show = false
                }) { Text("OK") }
            },
            dismissButton = {
                Row {
                    TextButton(onClick = { onChange(null); show = false }) { Text("Clear") }
                    TextButton(onClick = { show = false }) { Text("Cancel") }
                }
            }
        ) { DatePicker(state = state) }
    }
}
