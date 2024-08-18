package com.dev.shiftly.navigation

sealed class Screen(val route: String) {
    data object SignUp : Screen("signup")
    data object SignIn : Screen("signin")
    data object Main : Screen("main")
    data object Home : Screen("home")
    data object AddEmployee : Screen("addEmployee")
    data object EmployeeDetails : Screen("EmployeeDetails")
    data object Profile : Screen("profile")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}