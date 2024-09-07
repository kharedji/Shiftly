package com.dev.shiftly.navigation

sealed class Screen(val route: String) {
    data object SignUp : Screen("signup")
    data object SignIn : Screen("signin")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object AddEmployee : Screen("addEmployee")
    data object EmployeeDetails : Screen("EmployeeDetails")
    data object EmployeePlaySlips: Screen("EmployeePaySlips")
    data object Profile : Screen("profile")
    data object PaySlips : Screen("paySlips")
    data object CreatePaySlips : Screen("createPaySlips")
    data object AddShift : Screen("shift")

    data object EmployeeHome : Screen("employeeHome")



    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}