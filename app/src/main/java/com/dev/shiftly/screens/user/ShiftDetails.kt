package com.dev.shiftly.screens.user

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.shiftly.data.data_source.Shifts

@Composable
fun ShiftDetailsScreen(shift: Shifts) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = "Shift Details",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        // Details
        ShiftDetailRow(label = "Shift ID", value = shift.id)
        ShiftDetailRow(label = "Employee ID", value = shift.employeeId)
        ShiftDetailRow(label = "Employee Name", value = shift.employeeName)
        ShiftDetailRow(label = "Date", value = shift.date)
        ShiftDetailRow(label = "Start Time", value = shift.startTime)
        ShiftDetailRow(label = "End Time", value = shift.endTime)
        ShiftDetailRow(label = "Admin ID", value = shift.adminId)
    }
}

@Composable
fun ShiftDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
        Text(text = value, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewShiftDetailsScreen() {
    val sampleShift = Shifts(
        id = "S123",
        employeeId = "E123",
        employeeName = "John Doe",
        date = "2024-09-08",
        startTime = "09:00 AM",
        endTime = "05:00 PM",
        adminId = "A456"
    )
    ShiftDetailsScreen(shift = sampleShift)
}
