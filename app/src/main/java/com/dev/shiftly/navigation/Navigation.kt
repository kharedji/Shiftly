package com.dev.shiftly.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dev.shiftly.screens.admin.AddEmployee
import com.dev.shiftly.screens.admin.AddEmployeeShifts
import com.dev.shiftly.screens.main.MainScreen
import com.dev.shiftly.screens.signin.SignInScreen
import com.dev.shiftly.screens.signin.view_models.SignInViewModel
import com.dev.shiftly.screens.signup.view_models.SignUpViewModel
import com.google.firebase.auth.FirebaseAuth
import com.kharedji.memosphere.presentation.screens.signup.SignUpScreen

@Composable
fun Navigation(
    padding: PaddingValues = PaddingValues(),
    navController: NavHostController
) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) Screen.Main.route else Screen.SignIn.route
    NavHost(navController = navController, startDestination = startDestination) {
        composable(route = Screen.SignUp.route) {
            val viewModel: SignUpViewModel = hiltViewModel()
            SignUpScreen(
                paddingValues = padding,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screen.SignIn.route) {
            val viewModel: SignInViewModel = hiltViewModel()
            SignInScreen(
                paddingValues = padding,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = Screen.Main.route) {
            MainScreen(
                paddingValues = padding,
                navController = navController
            )
        }

        composable(route = Screen.AddEmployee.route) {
            AddEmployee(padding, navController)
        }

        composable(route = Screen.AddShift.route) {
            AddEmployeeShifts(padding,navController)
        }
    }
}