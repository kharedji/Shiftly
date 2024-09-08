package com.dev.shiftly.screens.signup.view_models

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.google.firebase.auth.AuthResult
import com.dev.shiftly.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(State<AuthResult>())
    val uiState: StateFlow<State<AuthResult>> = _uiState

    fun signUp(employee: Employee, context: Context) {
        viewModelScope.launch {
            repository.signUpUser(employee, context).onEach { state ->
                _uiState.emit(state)
            }.launchIn(viewModelScope)
        }
    }

    fun resetUiState() {
        viewModelScope.launch {
            _uiState.emit(State())
        }
    }
}