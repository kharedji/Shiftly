package com.dev.shiftly.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.admin.viewmodels.ShiftsViewModel
import com.google.gson.Gson

@Composable
fun EmployeeShifts(navController: NavController? = null){
    val context = LocalContext.current
    var currentUser = remember { mutableStateOf(Employee()) }
    val shiftsViewModel: ShiftsViewModel = hiltViewModel()
    val shiftsState = shiftsViewModel.allShifts.observeAsState()
    LaunchedEffect(key1 = Unit) {
        val userJson = SharedPrefsHelper.getInstance(context).getString("user")
        if (userJson != null) {
            currentUser.value = Gson().fromJson(userJson, Employee::class.java)
            shiftsViewModel.shiftsState(currentUser.value)
        }
    }

    when {
        shiftsState.value?.loading == true -> ProgressDialog()
        shiftsState.value?.error != null -> ErrorScreen(message = shiftsState.value?.error)
        shiftsState.value?.data != null -> EmployeeShiftsList(shifts = shiftsState.value!!.data!! , navController!!)
    }

}

@Composable
fun EmployeeShiftsList(
    shifts: List<Shifts>,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.padding(5.dp)) {
        items(shifts) { employee ->
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
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp))
            .padding(5.dp)
    ) {
        Text(
            text = "Date: ${employee.employeeName}",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = employee.date,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface

        )

        Text(
            text = "Start Time: ${employee.startTime}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "End Time: ${employee.endTime}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
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