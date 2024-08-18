package com.dev.shiftly.data.repository

import com.dev.shiftly.data.utils.State
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.dev.shiftly.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl() : UserRepository {

    override val auth = Firebase.auth

    /**
     * Sign up user with email and password
     * @param email
     * @param password
     * @return Flow<State<AuthResult>>
     */
    override suspend fun signUpUser(email: String, password: String) = flow {
        emit(State.loading())
        auth.createUserWithEmailAndPassword(email, password).await().run {
            emit(State.success(this))
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
    override suspend fun signInUser(email: String, password: String): Flow<State<AuthResult>> {
        return flow {
            emit(State.loading())
            auth.signInWithEmailAndPassword(email, password).await().run {
                emit(State.success(this))
            }
        }.catch {
            emit(State.error(it.message ?: UNKNOWN_ERROR))
        }
    }

    companion object {
        const val UNKNOWN_ERROR = "An unknown error occurred. Please try again later."
    }
}