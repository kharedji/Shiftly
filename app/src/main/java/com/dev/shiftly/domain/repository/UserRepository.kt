package com.dev.shiftly.domain.repository

import android.content.Context
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val auth: FirebaseAuth
    val database: FirebaseDatabase

    suspend fun signUpUser(employee: Employee, context: Context): Flow<State<AuthResult>>
    suspend fun signInUser(email: String, password: String): Flow<State<Employee>>
  
}