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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dev.shiftly.data.data_source.PaySlips

@Composable
fun PaySlipTable(paySlip: PaySlips, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Field", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
            Text("Value", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // PaySlip data rows
        PaySlipRow(label = "ID", value = paySlip.id)
        PaySlipRow(label = "Employee ID", value = paySlip.employeeId)
        PaySlipRow(label = "Date", value = paySlip.date)
        PaySlipRow(label = "Total Working Hours", value = paySlip.totalWorkingHours.toString())
        PaySlipRow(label = "Salary per Hour", value = paySlip.salaryPerHour.toString())
        PaySlipRow(label = "Total Salary", value = paySlip.totalSalary.toString())
        PaySlipRow(label = "Tax", value = paySlip.tax.toString())
        PaySlipRow(label = "Deduction", value = paySlip.deduction.toString())
    }
}

@Composable
fun PaySlipRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, modifier = Modifier.weight(1f), textAlign = TextAlign.Start)
        Text(text = value, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPaySlipTable() {
    val samplePaySlip = PaySlips(
        id = "PS001",
        employeeId = "E123",
        date = "2024-09-08",
        totalWorkingHours = 160f,
        salaryPerHour = 20f,
        totalSalary = 3200f,
        tax = 320f,
        deduction = 100f
    )
    PaySlipTable(paySlip = samplePaySlip, rememberNavController())
}
