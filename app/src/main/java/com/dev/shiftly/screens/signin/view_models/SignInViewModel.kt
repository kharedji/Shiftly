package com.dev.shiftly.screens.signin.view_models

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
class SignInViewModel @Inject constructor(
    private val repository: UserRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(State<Employee>())
    val uiState: StateFlow<State<Employee>> = _uiState

        fun signIn(email: String, password: String) {
            viewModelScope.launch {
                repository.signInUser(email, password).onEach { state ->
                    _uiState.value = state
                }.launchIn(viewModelScope)
            }
        }

        fun resetUiState() {
            viewModelScope.launch {
                _uiState.emit(State())
            }
        }
}