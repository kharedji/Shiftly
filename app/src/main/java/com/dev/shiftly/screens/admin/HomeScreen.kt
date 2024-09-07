package com.dev.shiftly.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: AdminViewModel = hiltViewModel()
    val employeeState by viewModel.employees.observeAsState(State.loading())
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
            }
        },
        content = {
            when {
                employeeState.loading -> ProgressDialog()
                employeeState.error != null -> ErrorScreen(message = employeeState.error)
                employeeState.data != null -> EmployeeList(employees = employeeState.data!!, it){
//                    navController.navigate(Screen.EmployeeDetails.route)

                }
            }

            if (showDialog) {
                AddEmployeeDialog(
                    onDismiss = { showDialog = false },
                    onAdd = { employee ->
                        val gson = Gson()
                        val currentUser = SharedPrefsHelper.getInstance(context).getString("user")
                        val currentEmployee = gson.fromJson(currentUser,Employee::class.java)
                        viewModel.addEmployee(employee,currentEmployee)
                    }
                )
            }
        }
    )
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
fun EmployeeList(
    employees: List<Employee>,
    paddingValues: PaddingValues,
    onClick: (Employee) -> Unit
) {
    LazyColumn(modifier = Modifier.padding(paddingValues)) {
        items(employees) { employee ->
            EmployeeItem(employee) { it ->
                onClick(it)
            }
        }
    }
}

@Composable
fun EmployeeItem(employee: Employee, onClick: (Employee) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp))
            .padding(5.dp)
            .clickable { onClick(employee) }
    ) {
        Text(
            text = employee.name,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "Position: ${employee.position}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, onAdd: (Employee) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var pay by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text("Add Employee", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = position,
                    onValueChange = { position = it },
                    label = { Text("Position") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = pay,
                    onValueChange = {  pay = it },
                    label = { Text("Pay Per Hour") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val employee = Employee(name = name, position = position, hourlyRate = pay.toFloat(), email = email, password = password, adminId = FirebaseAuth.getInstance().currentUser!!.uid)
                        onAdd(employee)
                        onDismiss()
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Add")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewEmployeeItem() {
    AddEmployeeDialog(onAdd = {

    }, onDismiss = {

    })
}