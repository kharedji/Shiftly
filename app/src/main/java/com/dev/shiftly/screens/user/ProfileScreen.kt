package com.dev.shiftly.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.dev.shiftly.R
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel

@Composable
fun ProfileScreen(
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(),
    employeeId: String
) {
    val viewModel: AdminViewModel = hiltViewModel()
    val employee = viewModel.getEmployeeById(employeeId).observeAsState()

    LaunchedEffect(key1 = Unit) {

    }

    Box(modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
        when {
            employee.value?.loading == true -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            employee.value?.data != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (employee.value?.data?.type != "admin") {
                        EmployeeDetails(employee = employee.value?.data!!) { employee ->
                            viewModel.updateEmployee(employee)
                        }
                    }else {
                        AdminDetails(employee = employee.value?.data!!)
                    }
                }
            }
            employee.value?.error != null -> {
                Text(
                    text = "Error: ${employee.value?.error}",
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EmployeeDetails(employee: Employee, onAvailabilityChange: (Employee) -> Unit){
    val isAvailable = remember { mutableStateOf(employee.isAvailable) }
    val painter = rememberAsyncImagePainter(
        employee.imageUri,
        error = painterResource(id = R.drawable.baseline_person_24)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Image(
        painter = painter,
        contentDescription = "Employee Image",
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = employee.name ?: "",
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Position: ${employee.position ?: ""}",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Email: ${employee.email ?: ""}",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Hourly Rate: ${employee.hourlyRate ?: ""}",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row (
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Are you Available?",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.width(20.dp))
        Switch(
            checked = isAvailable.value,
            onCheckedChange = {
                isAvailable.value = it
                employee.isAvailable = it
                onAvailabilityChange(employee)
            }
        )
    }
}

@Composable
fun AdminDetails(employee: Employee) {
    val painter = rememberAsyncImagePainter(
        employee.imageUri,
        error = painterResource(id = R.drawable.baseline_person_24)
    )
    Spacer(modifier = Modifier.height(16.dp))

    Image(
        painter = painter,
        contentDescription = "Employee Image",
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = employee.name ?: "",
        style = MaterialTheme.typography.titleLarge
    )

    Spacer(modifier = Modifier.height(10.dp))

    Text(
        text = "Company : ${employee.company ?: ""}",
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "Email: ${employee.email ?: ""}",
        style = MaterialTheme.typography.bodyLarge
    )
}