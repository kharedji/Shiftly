package com.dev.shiftly.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.utils.State
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class EmployeeRepository @Inject constructor() {
    fun getEmployees(): LiveData<State<List<Employee>>> {
        val database = FirebaseDatabase.getInstance()
        val stateLiveData = MutableLiveData<State<List<Employee>>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()

        runCatching {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
                ?: throw NullPointerException("Current user is not logged in")

            employeesRef.orderByChild("adminId").equalTo(currentUserId)
                .addValueEventListener(object : ValueEventListener {
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
        }.onFailure { exception ->
            stateLiveData.value = State.error(exception.message ?: "Unknown error")
        }

        return stateLiveData
    }


    fun addEmployee(employee: Employee, currentEmployee: Employee): LiveData<State<String>> {
        val database = FirebaseDatabase.getInstance()
        val stateLiveData = MutableLiveData<State<String>>()
        val employeesRef = database.getReference("employees")

        stateLiveData.value = State.loading()
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(employee.email, employee.password)
            .addOnSuccessListener {

                employee.id = it.user!!.uid
                val storage = FirebaseStorage.getInstance()
                val storageRef = storage.reference
                val fileRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")

                val uploadTask = fileRef.putFile(Uri.parse(employee.imageUri))
                uploadTask.addOnSuccessListener {

                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        employee.imageUri = downloadUri.toString()

                        employeesRef.child(employee.id).setValue(employee)
                            .addOnSuccessListener {
                                auth.signInWithEmailAndPassword(
                                    currentEmployee.email,
                                    currentEmployee.password
                                )
                                stateLiveData.value = State.success("Employee added successfully")
                            }
                            .addOnFailureListener { exception ->
                                stateLiveData.value =
                                    State.error(exception.message ?: "Unknown error")
                            }
                    }
                }.addOnFailureListener {
                    stateLiveData.value = State.error(it.message ?: "Unknown error")
                }

            }.addOnFailureListener {
                stateLiveData.value = State.error(it.message ?: "Unknown error")

            }
        return stateLiveData
    }

    fun getEmployeeById(employeeId: String): LiveData<State<Employee?>> {
        val database = FirebaseDatabase.getInstance()
        val stateLiveData = MutableLiveData<State<Employee?>>()
        val employeesRef = database.getReference("employees").child(employeeId)

        stateLiveData.value = State.loading()

        employeesRef.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val employee = dataSnapshot.getValue(Employee::class.java)
                stateLiveData.value = State.success(employee)
            } else {
                stateLiveData.value = State.error("Employee not found")
            }
        }.addOnFailureListener { exception ->
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
