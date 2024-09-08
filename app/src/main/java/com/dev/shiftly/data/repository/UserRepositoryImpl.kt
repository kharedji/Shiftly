package com.dev.shiftly.data.repository

import android.content.Context
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.dev.shiftly.domain.repository.UserRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl() : UserRepository {
    override val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    override val auth = Firebase.auth

    /**
     * Sign up user with email and password
     * @param email
     * @param password
     * @return Flow<State<AuthResult>>
     */
    override suspend fun signUpUser(employee: Employee, context: Context) = flow {
        emit(State.loading())
        auth.createUserWithEmailAndPassword(employee.email, employee.password).await().run {
            this.user?.uid?.let {
                val json = Gson().toJson(employee)
                SharedPrefsHelper.getInstance(context).putString("user",json)
                employee.id = it
                database.getReference("employees").child(it).setValue(employee)
                emit(State.success(this))
            }
        }
    }.catch {
        emit(State.error(it.message ?: UNKNOWN_ERROR))
    }

    /**
     * Sign in user with email and password
     * @param email
     * @param password
     * @return Flow<State<AuthResult>>
     */
    override suspend fun signInUser(email: String, password: String): Flow<State<Employee>> {
        return flow {
            emit(State.loading<Employee>())
            try {
                // Attempt to sign in with email and password
                val authResult = auth.signInWithEmailAndPassword(email, password).await()

                // Proceed if sign-in was successful
                val user = authResult.user
                if (user != null) {
                    val employeeData = database.getReference("employees").child(user.uid).get().await()
                        .getValue(Employee::class.java)

                    if (employeeData != null) {
                        emit(State.success(employeeData))
                    } else {
                        emit(State.error("Employee data not found"))
                    }
                } else {
                    emit(State.error("User not found"))
                }
            } catch (e: Exception) {
                // Handle exceptions and emit an error state
                emit(State.error("Sign-in failed: ${e.message}"))
            }
        }
    }

    companion object {
        const val UNKNOWN_ERROR = "An unknown error occurred. Please try again later."
    }
}