package com.dev.shiftly.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.screens.admin.viewmodels.EmployeeViewModel

@Composable
fun  HomeScreen(navController : NavController) {
    val viewModel: EmployeeViewModel = hiltViewModel()
    val employeeState by viewModel.employees.observeAsState(State.loading())

    when {
        employeeState.loading -> ProgressDialog()
        employeeState.error != null -> ErrorScreen(message = employeeState.error)
        employeeState.data != null -> EmployeeList(employees = employeeState.data!!)
    }
}
@Composable
fun ProgressDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color.White, shape = RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
fun ErrorScreen(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message ?: "Unknown Error", color = Color.Red)
    }
}

@Composable
fun EmployeeList(employees: List<Employee>) {
    LazyColumn {
        items(employees) { employee ->
            EmployeeItem(employee)
        }
    }
}

@Composable
fun EmployeeItem(employee: Employee) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = employee.name, style = MaterialTheme.typography.titleLarge)
        Text(text = employee.position, style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEmployeeItem() {
    EmployeeItem(employee = Employee(name = "John Doe", position = "Software Engineer"))
}