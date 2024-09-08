package com.dev.shiftly.navigation

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.shiftly.SharedPrefsHelper
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.data.data_source.PaySlips
import com.dev.shiftly.data.data_source.Shifts
import com.dev.shiftly.screens.admin.AddEmployee
import com.dev.shiftly.screens.admin.AddEmployeePaySlip
import com.dev.shiftly.screens.admin.AddEmployeeShifts
import com.dev.shiftly.screens.admin.PaySlips
import com.dev.shiftly.screens.main.MainScreen
import com.dev.shiftly.screens.signin.SignInScreen
import com.dev.shiftly.screens.signin.view_models.SignInViewModel
import com.dev.shiftly.screens.signup.view_models.SignUpViewModel
import com.dev.shiftly.screens.user.EmployeeDetails
import com.dev.shiftly.screens.user.EmployeeHome
import com.dev.shiftly.screens.user.EmployeeShifts
import com.dev.shiftly.screens.user.PaySlipTable
import com.dev.shiftly.screens.user.ProfileScreen
import com.dev.shiftly.screens.user.ShiftDetailsScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.kharedji.memosphere.presentation.screens.signup.SignUpScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun Navigation(
    padding: PaddingValues = PaddingValues(),
    navController: NavHostController,
    title: MutableState<String>
) {
    val context = navController.context
    var currentUser by remember { mutableStateOf<Employee?>(null) }
    LaunchedEffect(key1 = Unit) {
        val user = SharedPrefsHelper.getInstance(context).getString("user")
        currentUser = Gson().fromJson(user, Employee::class.java)
    }
    val startDestination =
        if (FirebaseAuth.getInstance().currentUser != null) {
            if (SharedPrefsHelper.getInstance(context).getString("user_type") == "admin") {
                Screen.Main.route
            } else {
                Screen.EmployeeHome.route
            }
        } else {
            Screen.SignIn.route
        }
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            title.value = "Sign Up"
            SignUpScreen(
                paddingValues = padding,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screen.SignIn.route) {
            val viewModel: SignInViewModel = hiltViewModel()
            title.value = "Sign In"
            SignInScreen(
                paddingValues = padding,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screen.Main.route) {
            title.value = "Shiftly"
            MainScreen(
                paddingValues = padding,
                navController = navController
            )
        }

        composable(route = Screen.AddEmployee.route) {
            title.value = "Add Employee"
            AddEmployee(padding, navController)
        }

        composable(route = Screen.AddShift.route) {
            title.value = "Add Shift"
            AddEmployeeShifts(padding, navController)
        }

        composable(route = Screen.EmployeePlaySlips.route) {

        }

        composable(route = Screen.Profile.withArgs("{id}")) {
            val employeeId = it.arguments?.getString("id") ?: ""
            title.value = "Profile"
            ProfileScreen(navController = navController, padding, employeeId = employeeId)
        }

        composable(route = Screen.EmployeeDetails.withArgs("{id}")) {
            val employeeId = it.arguments?.getString("id") ?: ""
            title.value = "Employee Details"
            EmployeeDetails(navController = navController, padding, employeeId)
        }

        composable(route = Screen.PaySlips.withArgs("{id}")) {
            title.value = "Pay Slips"
            val json = it.arguments?.getString("id") ?: ""
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val gson = Gson()
            val employee = gson.fromJson(decodedJson, Employee::class.java)
            PaySlips(navController = navController, padding, employee)
        }

        composable(route = Screen.CreatePaySlips.withArgs("{id}")) {
            title.value = "Create Pay Slip"
            val json = it.arguments?.getString("id") ?: ""
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())

            Log.e("aaa", "Navigation: ${json}")
            AddEmployeePaySlip(navController = navController, decodedJson, padding)
        }

        composable(route = Screen.EmployeeHome.route) {
            title.value = "Shiftly"
            EmployeeHome(navController = navController, paddingValues = padding)
        }

        composable(route = Screen.PaySlipDetails.withArgs("{details}")) {
            val json = it.arguments?.getString("details") ?: ""
            val gson = Gson()
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val payslip = gson.fromJson(decodedJson, PaySlips::class.java)
            PaySlipTable(payslip, navController)
        }

        composable(route = Screen.ShiftDetails.withArgs("{details}")) {
            val json = it.arguments?.getString("details") ?: ""
            val gson = Gson()
            val decodedJson = URLDecoder.decode(json, StandardCharsets.UTF_8.toString())
            val payslip = gson.fromJson(decodedJson, Shifts::class.java)
            ShiftDetailsScreen(payslip)
        }


    }
}