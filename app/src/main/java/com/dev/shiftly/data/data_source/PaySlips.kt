package com.dev.shiftly.data.data_source

class PaySlips(
    var id: String = "",
    var employeeId: String = "",
    var date: String = "",
    var totalWorkingHours: Float = 0f,
    var salaryPerHour : Float = 0f,
    var totalSalary : Float = 0f,
    var tax : Float = 0f,
    var deduction : Float = 0f,
)