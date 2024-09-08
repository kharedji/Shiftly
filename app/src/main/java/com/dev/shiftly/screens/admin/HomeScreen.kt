package com.dev.shiftly.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dev.shiftly.R
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.navigation.Screen
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
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Employee",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        content = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.background)
            ){
                when {
                    employeeState.loading -> ProgressDialog()
                    employeeState.error != null -> ErrorScreen(message = employeeState.error,)
                    employeeState.data != null -> EmployeeList(employees = employeeState.data!!, it) {
                        navController.navigate(Screen.EmployeeDetails.withArgs(it.id))
                    }
                }

                if (showDialog) {
                    AddEmployeeDialog(
                        onDismiss = { showDialog = false },
                        onAdd = { employee ->
                            val gson = Gson()
                            val currentUser = SharedPrefsHelper.getInstance(context).getString("user")
                            val currentEmployee = gson.fromJson(currentUser, Employee::class.java)
                            viewModel.addEmployee(employee, currentEmployee)
                        }
                    )
                }
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
fun ErrorScreen(message: String?, paddingValues: PaddingValues = PaddingValues()) {
    Box(
        modifier = Modifier.fillMaxSize().padding(paddingValues),
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
    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)) {
        items(employees) { employee ->
            EmployeeItem(employee) { it ->
                onClick(it)
            }
        }
    }
}

@Composable
fun EmployeeItem(employee: Employee, onClick: (Employee) -> Unit) {
    val painter = rememberAsyncImagePainter(
        model = employee.imageUri,
        error = painterResource(id = R.drawable.baseline_person_24) // Your error drawable resource
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(10.dp))
            .clickable { onClick(employee) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Image
        Image(
            painter = painter,
            contentDescription = "Employee Image",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp)) // Space between image and text

        Column {
            Text(
                text = employee.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = "Position: ${employee.position}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun AddEmployeeDialog(onDismiss: () -> Unit, onAdd: (Employee) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var position by remember { mutableStateOf("") }
    var pay by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

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

                ImagePickerWithCircularPlaceholder(
                    onImageSelected = { selectedUri -> imageUri = selectedUri },
                    imageUri = imageUri
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

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
                    onValueChange = { pay = it },
                    label = { Text("Pay Per Hour") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val employee = Employee(
                            name = name,
                            position = position,
                            hourlyRate = pay.toFloat(),
                            email = email,
                            password = password,
                            adminId = FirebaseAuth.getInstance().currentUser!!.uid,
                            type = "employee",
                            imageUri = imageUri.toString()
                        )
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

@Composable
fun ImagePickerWithCircularPlaceholder(onImageSelected: (Uri) -> Unit, imageUri: Uri?) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImageSelected(it) }
    }

    // Center the placeholder/image horizontally
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape) // Circular shape for both placeholder and image
            .border(2.dp, Color.Gray, CircleShape) // Circular border
            .clickable { launcher.launch("image/*") }, // Open image picker on click
        contentAlignment = Alignment.Center
    ) {
        // If image is selected, show it. Otherwise, show the placeholder
        if (imageUri != null) {
            // Display selected image
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "Selected Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Placeholder content
            Icon(
                imageVector = Icons.Default.Person, // Placeholder icon
                contentDescription = "Placeholder",
                tint = Color.Gray,
                modifier = Modifier.size(80.dp)
            )
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