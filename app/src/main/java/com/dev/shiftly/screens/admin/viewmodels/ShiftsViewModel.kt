package com.dev.shiftly.screens.admin.viewmodels

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.repository.ShiftsRepository
import com.dev.shiftly.data.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShiftsViewModel @Inject constructor(private val shiftsRepository: ShiftsRepository) :ViewModel() {
    private var _shiftsState: MutableLiveData<State<Boolean>> = MutableLiveData()
    val shiftsState: LiveData<State<Boolean>>
        get() = _shiftsState

     var allShifts : LiveData<State<List<Shifts>>> = shiftsRepository.getShifts()

    fun saveShift(shifts: Shifts){
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

}