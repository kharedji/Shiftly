package com.dev.shiftly.screens.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
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
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel

@Composable
fun EmployeeDetails(
    navController: NavController? = null,
    paddingValues: PaddingValues = PaddingValues(),
    employeeId: String,
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
                    val painter = rememberAsyncImagePainter(
                        employee.value?.data?.imageUri,
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
                        text = employee.value?.data?.name ?: "",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Position: ${employee.value?.data?.position ?: ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Email: ${employee.value?.data?.email ?: ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hourly Rate: ${employee.value?.data?.hourlyRate ?: ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Is Available?: ${employee.value?.data?.isAvailable ?: ""}",
                        style = MaterialTheme.typography.bodyLarge
                    )
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