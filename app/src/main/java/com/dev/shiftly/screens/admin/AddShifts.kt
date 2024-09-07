package com.dev.shiftly.screens.admin

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel
import com.dev.shiftly.screens.admin.viewmodels.ShiftsViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeShifts(
    paddingValues: PaddingValues,
    navController: NavController? = null,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val shiftViewModel: ShiftsViewModel = hiltViewModel()

    val shiftState = shiftViewModel.shiftsState.observeAsState()
    val employeeState by viewModel.employees.observeAsState(State.loading())


    val (selectedEmployee, setSelectedEmployee) = remember { mutableStateOf<Employee?>(null) }
    val (showEmployeeDialog, setShowEmployeeDialog) = remember { mutableStateOf(false) }
    val employees = remember { mutableStateListOf<Employee>() }

    val selectedDate = rememberDatePickerState(
        initialSelectedDateMillis = Date().time
    )
    val mTime = remember { mutableStateOf("") }
    val mEndTime = remember { mutableStateOf("") }

    val showDatePicker = remember { mutableStateOf(false) }
    val showStartTimePicker = remember { mutableStateOf(false) }
    val showEndTimePicker = remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }


    val context = LocalContext.current
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]


    when {
        employeeState.loading || shiftState.value?.loading == true -> ProgressDialog()
        employeeState.error != null -> ErrorScreen(message = employeeState.error)
        employeeState.data != null -> {
            employees.clear()
            employees.addAll(employeeState.data!!)
        }
        shiftState.value?.data == true -> navController?.navigateUp()
        shiftState.value?.error != null -> Toast.makeText(
            context,
            shiftState.value?.error.toString(),
            Toast.LENGTH_SHORT
        ).show()
    }

    AddShift(
        paddingValues = paddingValues, selectedEmployee, selectedDate,
        showDatePicker, showStartTimePicker, showEndTimePicker, mTime, mEndTime, { it ->
            shiftViewModel.saveShift(it)
//            navController?.navigateUp()
        }, {
            setShowEmployeeDialog(true)
        }
    )

    if (showEmployeeDialog) {
        EmployeeSelectionDialog(
            employees = employees,
            onEmployeeSelected = {
                setSelectedEmployee(it)
                setShowEmployeeDialog(false)
            },
            onDismissRequest = { setShowEmployeeDialog(false) }
        )
    }

    // Date picker dialog
    if (showDatePicker.value) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker.value = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = selectedDate)
        }
    }

    // Start time picker dialog
    if (showStartTimePicker.value) {
        val mTimePickerDialog = TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                // Format hour and minute to always have two digits
                val formattedHour = String.format("%02d", hour)
                val formattedMinute = String.format("%02d", minute)
                mTime.value = "$formattedHour:$formattedMinute"
            }, mHour, mMinute, false
        )

        mTimePickerDialog.show()
        showStartTimePicker.value = false
    }

    if (showEndTimePicker.value) {
        val mTimePickerDialog = TimePickerDialog(
            context,
            { _, hour: Int, minute: Int ->
                val formattedHour = String.format("%02d", hour)
                val formattedMinute = String.format("%02d", minute)
                mEndTime.value = "$formattedHour:$formattedMinute"
            }, mHour, mMinute, false
        )

        mTimePickerDialog.show()
        showEndTimePicker.value = false
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShift(
    paddingValues: PaddingValues,
    selectedEmployee: Employee?,
    selectedDate: DatePickerState?,
    showDatePicker: MutableState<Boolean>,
    showStartTimePicker: MutableState<Boolean>,
    showEndTimePicker: MutableState<Boolean>,
    mTime: MutableState<String>,
    mEndTime: MutableState<String>,
    onClick: (Shifts) -> Unit,
    onEmployeeSelect: () -> Unit
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        // Employee selection
        OutlinedTextField(
            value = selectedEmployee?.name ?: "",
            onValueChange = { },
            enabled = false,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy()
            ),
            readOnly = true,
            label = { Text("Select Employee") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    onEmployeeSelect()
                }
        )

        // Date selection
        OutlinedTextField(
            value = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.ENGLISH
            ).format(Date(selectedDate?.selectedDateMillis ?: System.currentTimeMillis())),
            onValueChange = {},
            enabled = false,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy()
            ),
            readOnly = true,
            label = { Text("Select Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { showDatePicker.value = true }
        )

        // Start time selection
        OutlinedTextField(
            value = "${mTime.value}",
            onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy()
            ),
            label = { Text("Select Start Time") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { showStartTimePicker.value = true }
        )

        // End time selection
        OutlinedTextField(
            value = mEndTime.value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            colors = TextFieldDefaults.textFieldColors(
                disabledTextColor = LocalContentColor.current.copy(),
                disabledLabelColor = MaterialTheme.colorScheme.onSurface.copy()
            ),
            label = { Text("Select End Time") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { showEndTimePicker.value = true }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val date = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.ENGLISH
                ).format(Date(selectedDate!!.selectedDateMillis!!))
                val start = mTime.value
                val end = mEndTime.value
                val employee = selectedEmployee?.name
                val employeeID = selectedEmployee?.id
                val shift = Shifts("", employeeID!!, employee!!, date, start, end)
                onClick(shift)
            },
            Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp)
        ) {
            Text(text = "Add Shift")
        }
    }

    fun addShift(employee: Employee, date: String, startTime: String, endTime: String) {

    }

}

@Composable
fun EmployeeSelectionDialog(
    employees: List<Employee>,
    onEmployeeSelected: (Employee) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column {
                Text(
                    text = "Select Employee",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                LazyColumn {
                    items(employees) { employee ->
                        ListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onEmployeeSelected(employee)
                                    onDismissRequest()
                                }
                                .padding(horizontal = 16.dp, vertical = 5.dp),
                            headlineContent = {
                                Column {
                                    Text(text = employee.name)
                                    Text(
                                        text = employee.position,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.height(5.dp))
                                    HorizontalDivider()
                                }

                            }
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("NewApi", "UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun PreviewItemDialog() {
//    AddShift(
//        PaddingValues(10.dp), Employee("ss", "sss", "Ss"), DatePickerState(Locale.ENGLISH),
//        mutableStateOf(true), mutableStateOf(true), mutableStateOf(true), mutableStateOf("Sfd"),
//        ````mutableStateOf("ss",{
//
//        },{
//
//        })
//    )
}



