package com.dev.shiftly.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.admin.viewmodels.PaySlipsViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun PaySlips(navController: NavController? = null, paddingValues: PaddingValues, employee: Employee) {

    val paySlipsViewModel: PaySlipsViewModel = hiltViewModel()
    val employeeState by paySlipsViewModel.allEmployeePaySlips(employee.id).observeAsState()
    Scaffold(
        modifier = Modifier.padding(paddingValues),
        floatingActionButton = {

            FloatingActionButton(onClick = {
                val gson = Gson()
                val employeesJson = gson.toJson(employee)
                val encodedJson = URLEncoder.encode(employeesJson, StandardCharsets.UTF_8.toString())

                navController?.navigate(Screen.CreatePaySlips.withArgs("${encodedJson}")) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Employee",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = {
            when {
                employeeState?.loading == true -> ProgressDialog()
                employeeState?.error != null -> ErrorScreen(message = employeeState!!.error, it)
                employeeState?.data != null -> PaySlipsList(
                    paySlips = employeeState!!.data!!,
                    it,
                    navController!!
                )
            }
        }
    )
}

@Composable
fun PaySlipsList(
    paySlips: List<PaySlips>,
    paddingValues: PaddingValues,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(paySlips) { employee ->
            PaySlipItem(employee) { it ->
//                navController.navigate(Screen.EmployeeDetails.route)
            }
        }
    }
}

@Composable
fun PaySlipItem(paySlip: PaySlips, onClick: (PaySlips) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
            .padding(5.dp)
            .clickable { onClick(paySlip) }
    ) {
        Text(
            text = paySlip.date,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "Working Hours: ${paySlip.totalWorkingHours}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "Salary Per Hour: ${paySlip.salaryPerHour}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "Total Salary: ${paySlip.totalSalary}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PaySlipsPreview() {

}