package com.dev.shiftly.domain.repository

import com.dev.shiftly.data.utils.State
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    val auth: FirebaseAuth

    suspend fun signUpUser(email: String, password: String): Flow<State<AuthResult>>
    suspend fun signInUser(email: String, password: String): Flow<State<AuthResult>>
  
}