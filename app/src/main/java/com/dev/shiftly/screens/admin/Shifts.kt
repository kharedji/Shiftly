package com.dev.shiftly.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.admin.viewmodels.ShiftsViewModel

@Composable
fun Shift(navController: NavController) {
    val viewModel: ShiftsViewModel = hiltViewModel()
    val employeeState by viewModel.allShifts.observeAsState(State.loading())
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.AddShift.route) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Employee",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = {
            when {
                employeeState.loading -> ProgressDialog()
                employeeState.error != null -> ErrorScreen(message = employeeState.error)
                employeeState.data != null -> ShiftsList(
                    employees = employeeState.data!!,
                    it,
                    navController
                )
            }


        }
    )
}

    @Composable
    fun ShiftsList(
        employees: List<Shifts>,
        paddingValues: PaddingValues,
        navController: NavController
    ) {
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(employees) { employee ->
                ShiftItem(employee) { it ->
                    navController.navigate(Screen.EmployeeDetails.route)
                }
            }
        }
    }

    @Composable
    fun ShiftItem(employee: Shifts, onClick: (Shifts) -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
                .padding(5.dp)
        ) {
            Text(
                text = employee.employeeName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Date: ${employee.date}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer

            )

            Text(
                text = "Start Time: ${employee.startTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "End Time: ${employee.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }

