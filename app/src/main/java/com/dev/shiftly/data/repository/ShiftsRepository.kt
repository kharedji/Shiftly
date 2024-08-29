package com.dev.shiftly.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.data.utils.State
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ShiftsRepository @Inject constructor(){
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("shifts")

    suspend fun saveShift(shift: Shifts): Result<Unit> {
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
    fun getShifts(): LiveData<State<List<Shifts>>> {
        val database =FirebaseDatabase.getInstance()

        val stateLiveData = MutableLiveData<State<List<Shifts>>>()
        val shiftsRef = database.getReference("shifts")

        stateLiveData.value = State.loading()

        shiftsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val shifts = mutableListOf<Shifts>()
                for (shiftsnapshot in snapshot.children) {
                    val employee = shiftsnapshot.getValue(Shifts::class.java)
                    employee?.let { shifts.add(it) }
                }
                if (shifts.isNotEmpty()) {
                    stateLiveData.value = State.success(shifts)
                }else{
                    stateLiveData.value = State.error("No Shifts")

                }
            }

            override fun onCancelled(error: DatabaseError) {
                stateLiveData.value = State.error(error.message)
            }
        })

        return stateLiveData
    }
}
