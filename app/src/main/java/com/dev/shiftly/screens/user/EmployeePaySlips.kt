package com.dev.shiftly.screens.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.screens.admin.PaySlipsList
import com.dev.shiftly.screens.admin.viewmodels.PaySlipsViewModel
import com.google.gson.Gson

@Composable
fun EmployeePaySlips(navController: NavController? = null){

    val context = LocalContext.current
    var currentUser = remember { mutableStateOf(Employee()) }
    val paySlipsViewModel: PaySlipsViewModel = hiltViewModel()
    val paySlipState by paySlipsViewModel.allEmployeePaySlips(currentUser.value.id).observeAsState()
    LaunchedEffect(key1 = Unit) {
        val userJson = SharedPrefsHelper.getInstance(context).getString("user")
        if (userJson != null) {
            currentUser.value = Gson().fromJson(userJson, Employee::class.java)
        }
    }

    when {
        paySlipState?.loading == true -> com.dev.shiftly.screens.admin.ProgressDialog()
        paySlipState?.error != null -> com.dev.shiftly.screens.admin.ErrorScreen(message = paySlipState!!.error)
        paySlipState?.data != null -> PaySlipsList(
            paySlips = paySlipState!!.data!!,
            navController!!
        )
    }
}

@Composable
fun PaySlipsList(
    paySlips: List<PaySlips>,
    navController: NavController
) {
    LazyColumn(modifier = Modifier.padding(5.dp)) {
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
            .background(MaterialTheme.colorScheme.onBackground, RoundedCornerShape(10.dp))
            .padding(5.dp)
            .clickable { onClick(paySlip) }
    ) {
        Text(
            text = paySlip.date,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "Working Hours: ${paySlip.totalWorkingHours}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "Salary Per Hour: ${paySlip.salaryPerHour}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
        Text(
            text = "Total Salary: ${paySlip.totalSalary}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.surface
        )
    }
}