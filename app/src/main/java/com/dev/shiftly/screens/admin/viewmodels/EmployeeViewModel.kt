package com.dev.shiftly.screens.admin.viewmodels

import androidx.lifecycle.ViewModel
import com.dev.shiftly.data.repository.EmployeeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EmployeeViewModel @Inject constructor(
    private val repository: EmployeeRepository
): ViewModel() {

}