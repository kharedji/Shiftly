package com.dev.shiftly.screens.admin.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.repository.ShiftsRepository
import com.dev.shiftly.data.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel
class ShiftsViewModel @Inject constructor(private val shiftsRepository: ShiftsRepository) :
    ViewModel() {
    private var _shiftsState: MutableLiveData<State<Boolean>> = MutableLiveData()
    val shiftsState: LiveData<State<Boolean>>
        get() = _shiftsState

    var allShifts: LiveData<State<List<Shifts>>> = shiftsRepository.getShifts()

    fun shiftsState(employee: Employee) {
        allShifts = shiftsRepository.getEmployeeShifts(employee)
    }

    fun saveShift(shifts: Shifts) {
        _shiftsState.value = State.loading()
        viewModelScope.launch {
            val result = shiftsRepository.saveShift(shifts)
            result.onSuccess {
                _shiftsState.value = State.success(true)
            }.onFailure {
                _shiftsState.value = State.error(it.message.toString())
            }
        }
    }


    fun calculateMonthlyWorkingHours(
        shifts: List<Shifts>,
        selectedMonth: String,
        selectedYear: String
    ): Long {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val filteredShifts = shifts.filter { shift ->
            val shiftDate = LocalDate.parse(shift.date, dateFormatter)
            val monthName = Month.of(shiftDate.monthValue).name.lowercase().capitalize()
            monthName.equals(selectedMonth, true) && shiftDate.year.toString() == selectedYear
        }

        var totalMinutes: Long = 0

        for (shift in filteredShifts) {
            val startDate = LocalDate.parse(shift.date, dateFormatter)

            var startTime: LocalTime
            var endTime: LocalTime

            try {
                startTime = LocalTime.parse(shift.startTime, timeFormatter)
            } catch (e: DateTimeParseException) {
                shift.startTime = "0${shift.startTime}"
                startTime = LocalTime.parse(shift.startTime)
            }
            try {
                endTime = LocalTime.parse(shift.endTime, timeFormatter)
            } catch (e: DateTimeParseException) {
                shift.endTime = "0${shift.startTime}"
                endTime = LocalTime.parse(shift.startTime)
            }

            val startDateTime = startDate.atTime(startTime)
            val endDateTime = startDate.atTime(endTime)

            val minutes = ChronoUnit.MINUTES.between(startDateTime, endDateTime)
            totalMinutes += minutes
        }

        return totalMinutes / 60 // Convert total minutes to hours
    }

}