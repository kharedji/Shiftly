package com.dev.shiftly.screens.admin.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.data.repository.EmployeePaySlipRepository
import com.dev.shiftly.data.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaySlipsViewModel @Inject constructor(private val employeePaySlipRepos: EmployeePaySlipRepository) :
    ViewModel() {
    private var _saveEmployeeSlipState: MutableLiveData<State<Boolean>> = MutableLiveData()
    val saveEmployeeSlipState: LiveData<State<Boolean>>
        get() = _saveEmployeeSlipState

    private var _payslipState: MutableLiveData<State<List<PaySlips>>> = MutableLiveData()
    val paySlipState: LiveData<State<List<PaySlips>>>
    get() = _payslipState


    fun allEmployeePaySlips(id: String): LiveData<State<List<PaySlips>>> = employeePaySlipRepos.getPaySlip(id)

    fun getPayslipsById(id: String) {
        _payslipState.value = State.loading()
        viewModelScope.launch {
            val result = employeePaySlipRepos.getPaySlipById(id)
            result.onSuccess {
                _payslipState.value = State.success(it)
            }.onFailure {
                _payslipState.value = State.error(it.message.toString())
            }
        }
    }

    fun savePaySlip(paySlips: PaySlips) {
        _saveEmployeeSlipState.value = State.loading()
        viewModelScope.launch {
            val result = employeePaySlipRepos.savePaySlip(paySlips)
            result.onSuccess {
                _saveEmployeeSlipState.value = State.success(true)
            }.onFailure {
                _saveEmployeeSlipState.value = State.error(it.message.toString())
            }
        }
    }
}