package com.dev.shiftly.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.admin.EmployeeList
import com.dev.shiftly.screens.admin.ErrorScreen
import com.dev.shiftly.screens.admin.ProgressDialog
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmployeesPaySlips(navController: NavController, employeeViewModel: AdminViewModel = hiltViewModel()) {
    val employeeState by employeeViewModel.employees.observeAsState(State.loading())

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Employees Pay Slips")
            })
        },
        content = {
            when {
                employeeState.loading -> ProgressDialog()
                employeeState.error != null -> ErrorScreen(message = employeeState.error)
                employeeState.data != null -> EmployeeList(employees = employeeState.data!!, it){ employeee->
                    val gson = Gson()
                    val string = gson.toJson(employeee)
                    val encodedJson = URLEncoder.encode(string, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.PaySlips.withArgs(encodedJson))
                }
            }
        }
    )

}

