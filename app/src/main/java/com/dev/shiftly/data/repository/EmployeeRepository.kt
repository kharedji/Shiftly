package com.dev.shiftly.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import javax.inject.Inject

class EmployeeRepository @Inject constructor() {
    fun getEmployees(): LiveData<State<List<Employee>>> {
        val database =FirebaseDatabase.getInstance()

        val stateLiveData = MutableLiveData<State<List<Employee>>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()

        employeesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val employees = mutableListOf<Employee>()
                for (employeeSnapshot in snapshot.children) {
                    val employee = employeeSnapshot.getValue(Employee::class.java)
                    employee?.let { employees.add(it) }
                }
                stateLiveData.value = State.success(employees)
            }

            override fun onCancelled(error: DatabaseError) {
                stateLiveData.value = State.error(error.message)
            }
        })

        return stateLiveData
    }

    fun addEmployee(employee: Employee): LiveData<State<String>> {
        val database = FirebaseDatabase.getInstance()
        val stateLiveData = MutableLiveData<State<String>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()

        val key = employeesRef.push().key
        if (key == null) {
            stateLiveData.value = State.error("Could not generate a new key")
            return stateLiveData
        }

        employee.id = key
        employeesRef.child(key).setValue(employee)
            .addOnSuccessListener {
                stateLiveData.value = State.success("Employee added successfully")
            }
            .addOnFailureListener { exception ->
                stateLiveData.value = State.error(exception.message ?: "Unknown error")
            }

        return stateLiveData
    }

    fun updateEmployee(employee: Employee): LiveData<State<String>> {
        val database = FirebaseDatabase.getInstance();
        val stateLiveData = MutableLiveData<State<String>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()

        employeesRef.child(employee.id).setValue(employee)
            .addOnSuccessListener {
                stateLiveData.value = State.success("Employee updated successfully")
            }
            .addOnFailureListener { exception ->
                stateLiveData.value = State.error(exception.message ?: "Unknown error")
            }

        return stateLiveData
    }

    fun deleteEmployee(employeeId: String): LiveData<State<String>> {
        val database = FirebaseDatabase.getInstance()
        val stateLiveData = MutableLiveData<State<String>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()

        employeesRef.child(employeeId).removeValue()
            .addOnSuccessListener {
                stateLiveData.value = State.success("Employee deleted successfully")
            }
            .addOnFailureListener { exception ->
                stateLiveData.value = State.error(exception.message ?: "Unknown error")
            }

        return stateLiveData
    }

}
