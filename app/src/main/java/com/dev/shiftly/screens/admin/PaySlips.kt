package com.dev.shiftly.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

@Composable
fun PaySlips(navController: NavController, employee: Employee) {

    val paySlipsViewModel: PaySlipsViewModel = hiltViewModel()
    val employeeState by paySlipsViewModel.allEmployeePaySlips(employee.id).observeAsState()
    Scaffold(
        floatingActionButton = {

            FloatingActionButton(onClick = {
                val gson = Gson()
                val employeesJson = gson.toJson(employee)

                navController.navigate(Screen.CreatePaySlips.withArgs("${employeesJson}")) }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
            }
        },
        content = {
            when {
                employeeState?.loading == true -> ProgressDialog()
                employeeState?.error != null -> ErrorScreen(message = employeeState!!.error)
                employeeState?.data != null -> PaySlipsList(
                    paySlips = employeeState!!.data!!,
                    it,
                    navController
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
                navController.navigate(Screen.EmployeeDetails.route)
            }
        }
    }
}

@Composable
fun PaySlipItem(paySlip: PaySlips, onClick: (PaySlips) -> Unit) {
    Column(modifier = Modifier.padding(16.dp).clickable {
        onClick(paySlip)
    }) {
        Text(text = paySlip.date, style = MaterialTheme.typography.titleLarge)
        Text(text = paySlip.date, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun EmployeeShifts(navController: NavController){
//    val viewModel :PaySlipsViewModel = hiltViewModel()
//    val employeeState by viewModel.allShifts(id).observeAsState()
//    Scaffold(
//        floatingActionButton = {
//            FloatingActionButton(onClick = { navController.navigate(Screen.AddShift.route) }) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Employee")
//            }
//        },
//        content = {
//            when {
//                employeeState?.loading == true -> ProgressDialog()
//                employeeState?.error != null -> ErrorScreen(message = employeeState!!.error)
//                employeeState?.data != null -> PaySlipsList(
//                    paySlips = employeeState!!.data!!,
//                    it,
//                    navController
//                )
//            }
//        }
//    )

}