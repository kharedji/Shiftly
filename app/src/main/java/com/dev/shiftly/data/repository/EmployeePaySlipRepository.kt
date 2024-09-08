package com.dev.shiftly.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class EmployeePaySlipRepository @Inject constructor() {
    fun getPaySlip(employeeId: String): LiveData<State<List<PaySlips>>> {
        val database = FirebaseDatabase.getInstance()

        val stateLiveData = MutableLiveData<State<List<PaySlips>>>()
        val shiftsRef = database.getReference("paySlips")

        stateLiveData.value = State.loading()

        shiftsRef.orderByChild("employeeId").equalTo(employeeId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shifts = mutableListOf<PaySlips>()
                for (shiftsnapshot in snapshot.children) {
                    val employee = shiftsnapshot.getValue(PaySlips::class.java)
                    employee?.let { shifts.add(it) }
                }
                if (shifts.isNotEmpty()) {
                    stateLiveData.value = State.success(shifts)
                } else {
                    stateLiveData.value = State.error("No Shifts")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                stateLiveData.value = State.error(error.message)
            }
        })

        return stateLiveData
    }

    suspend fun getPaySlipById(employeeId: String): Result<List<PaySlips>> {
        val database = FirebaseDatabase.getInstance()
        val paySlipsRef = database.getReference("paySlips")

        return try {
            // Perform a query to get the pay slips for the specific employee ID
            val snapshot = paySlipsRef
                .orderByChild("employeeId")
                .equalTo(employeeId)
                .get()
                .await() // Await the query result

            // Extract the list of PaySlips from the snapshot
            val paySlips = snapshot.children.mapNotNull { it.getValue(PaySlips::class.java) }

            if (paySlips.isNotEmpty()) {
                Result.success(paySlips)
            } else {
                Result.failure(Exception("No PaySlips found for the employee"))
            }
        } catch (e: Exception) {
            Result.failure(e) // Return the failure with the exception
        }
    }

    suspend fun savePaySlip(shift: PaySlips): Result<Unit> {
         val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("paySlips")

        return try {
            val snapshot = dbRef
                .orderByChild("employeeId")
                .equalTo(shift.employeeId)
                .get()
                .await()

            val existingShift = snapshot.children.find {
                it.child("date").getValue(String::class.java) == shift.date
            }

            if (existingShift != null) {
                Result.failure(Exception("Employee already has a shift on this day"))
            } else {
                val key = dbRef.push().key ?: return Result.failure(Exception("Failed to generate key"))
                shift.id = key
                dbRef.child(key).setValue(shift).await()
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}