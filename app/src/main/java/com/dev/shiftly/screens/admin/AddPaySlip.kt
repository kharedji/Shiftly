package com.dev.shiftly.screens.admin

import android.annotation.SuppressLint
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.screens.admin.viewmodels.PaySlipsViewModel
import com.dev.shiftly.screens.admin.viewmodels.ShiftsViewModel
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("UnrememberedMutableState")
@Composable
fun AddEmployeePaySlip(
    navController: NavController,
    json: String,
    paddingValues: PaddingValues
) {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val years = (year - 50..year).toList()
    var expanded by remember { mutableStateOf(false) }

    // State variables
    var selectedMonth = remember { mutableStateOf("") }
    val selectedYear = remember { mutableStateOf("") }
    var workingHours = remember { mutableStateOf("0") }
    var overTime by remember { mutableStateOf("0") }
    var deductions by remember { mutableStateOf("0") }
    var tax by remember { mutableStateOf("0") }
    var result by remember { mutableFloatStateOf(0f) }
    val verticalScroll = rememberScrollState()
    val list = remember { mutableListOf<Shifts>() }

    val gson = Gson()
    val employee = remember {
        mutableStateOf(Employee())
    }
    var payPerHour by remember { mutableFloatStateOf(0f) }

    var paySlip = remember {
        mutableStateOf(PaySlips())
    }


    // Get today's month and year


    // ViewModel and State
    val viewModel: ShiftsViewModel = hiltViewModel()
    val paySlipsViewModel: PaySlipsViewModel = hiltViewModel()
    val paySlipState by paySlipsViewModel.saveEmployeeSlipState.observeAsState()


    LaunchedEffect(Unit) {
        employee.value = gson.fromJson(json, Employee::class.java)
        payPerHour = employee.value.hourlyRate
        paySlip.value.employeeId = employee.value.id
        viewModel.shiftsState(employee.value)
        selectedMonth.value = SimpleDateFormat("MM").format(Date())
        selectedYear.value = SimpleDateFormat("yyyy").format(Date())
    }
    val state = viewModel.allShifts.observeAsState()

    // Calculate total salary whenever relevant values change
    LaunchedEffect(workingHours.value, overTime, payPerHour, tax, deductions) {
        val totalWorkingHours = workingHours.value.toFloatOrNull() ?: 0f
        val overtimeHours = overTime.toFloatOrNull() ?: 0f
        val payRate = payPerHour ?: 0f
        val taxAmount = tax.toFloatOrNull() ?: 0f
        val deductionAmount = deductions.toFloatOrNull() ?: 0f

        val totalPay = (totalWorkingHours + overtimeHours) * payRate
        result = totalPay - taxAmount - deductionAmount
        paySlip.value.totalWorkingHours = totalWorkingHours
        paySlip.value.salaryPerHour = payRate
        paySlip.value.totalSalary = totalPay
        paySlip.value.tax = taxAmount
        paySlip.value.deduction = deductionAmount
        paySlip.value.date = SimpleDateFormat("dd/MM/yyyy").format(Date())
    }

    LaunchedEffect(selectedMonth.value, selectedYear.value) {
        workingHours.value =
            viewModel.calculateMonthlyWorkingHours(list, selectedMonth.value, selectedYear.value)
                .toString()
    }

    LaunchedEffect(key1 = state.value?.data) {
        if (state.value?.data?.isNotEmpty() == true) {
            list.clear()
            list.addAll(state.value!!.data ?: emptyList())
            workingHours.value = viewModel.calculateMonthlyWorkingHours(
                list,
                selectedMonth.value,
                selectedYear.value
            ).toString()
        }
    }
    // UI
    when {
        state.value?.data?.isNotEmpty() == true -> {
//            list.clear()
//            list.addAll(state.value!!.data ?: emptyList())

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(verticalScroll),
                verticalArrangement = Arrangement.Top
            ) {
                OutlinedTextField(
                    value = workingHours.value,
                    onValueChange = { workingHours.value = it },
                    enabled = true,
                    label = { Text("Working Hours") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = payPerHour.toString(),
                    onValueChange = { payPerHour = it.toFloat() },
                    label = { Text("Pay Per Hour") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = overTime,
                    onValueChange = { overTime = it },
                    label = { Text("Over Time") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = tax,
                    onValueChange = { tax = it },
                    label = { Text("Tax") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = deductions,
                    onValueChange = { deductions = it },
                    label = { Text("Deductions") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                MonthDropdown(selectedMonth.value) { newMonth ->
                    selectedMonth.value = newMonth
                }
                Spacer(modifier = Modifier.height(16.dp))


                Box {
                    TextField(
                        value = selectedYear.value,
                        onValueChange = { selectedYear.value = it },
                        enabled = false,
                        label = { Text("Year") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        years.forEach { year ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = year.toString())
                                }, onClick = {
                                    selectedYear.value = year.toString()
                                    expanded = false
                                })


                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = result.toString(),
                    onValueChange = { result = it.toFloat() },
                    enabled = false,
                    label = { Text("Salary") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    paySlipsViewModel.savePaySlip(paySlip.value)
                }) {
                    Text("Submit")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        else -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No shifts available to calculate payslip!")
            }
        }
    }
}

@Composable
fun MonthDropdown(selectedMonth: String, onMonthSelected: (String) -> Unit) {
    val months = listOf(
        "Jan", "Feb", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )
    var expanded by remember { mutableStateOf(false) }

    Box {
        TextField(
            value = selectedMonth,
            onValueChange = { onMonthSelected(it) },
            label = { Text("Month") },
            enabled = false,
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            months.forEach { month ->
                DropdownMenuItem(text = {
                    Text(text = month)
                }, onClick = {
                    onMonthSelected(month)
                    expanded = false
                })
            }
        }
    }
}

@Composable
fun YearDropdown(selectedYear: TextFieldValue, onYearSelected: (String) -> Unit) {


}


