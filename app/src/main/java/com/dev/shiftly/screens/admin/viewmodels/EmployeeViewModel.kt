package com.dev.shiftly.screens.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.repository.EmployeeRepository
import com.dev.shiftly.data.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(private val repository: EmployeeRepository) :
    ViewModel() {

    val employees: LiveData<State<List<Employee>>> = repository.getEmployees()

    fun addEmployee(employee: Employee): LiveData<State<String>> {
        return repository.addEmployee(employee)
    }

    fun updateEmployee(employee: Employee): LiveData<State<String>> {
        return repository.updateEmployee(employee)
    }

    fun deleteEmployee(employeeId: String): LiveData<State<String>> {
        return repository.deleteEmployee(employeeId)
    }
}
