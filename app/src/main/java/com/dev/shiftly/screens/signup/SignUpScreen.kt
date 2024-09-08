package com.kharedji.memosphere.presentation.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.signup.view_models.SignUpViewModel

@Composable
fun SignUpScreen(
    paddingValues: PaddingValues = PaddingValues(),
    navController: NavHostController? = null,
    viewModel: SignUpViewModel? = null
) {
    val context = LocalContext.current
    val uiState = viewModel?.uiState?.collectAsState()

    var email by rememberSaveable {
        mutableStateOf("")
    }

    var name by rememberSaveable {
        mutableStateOf("")
    }

    var company by rememberSaveable {
        mutableStateOf("")
    }
    var password by rememberSaveable {
        mutableStateOf("")
    }

    var isEmailValid by rememberSaveable {
        mutableStateOf(false)
    }

    var isPasswordValid by rememberSaveable {
        mutableStateOf(false)
    }

    val localFocusManager = LocalFocusManager.current

    // non-null data in the uiState means that the sign up request
    // was processed successfully and the account has been created.
    // Therefore the NavController can route to the MainScreen.
    uiState?.value?.data?.let {
        navController?.apply{
            navigate(Screen.Main.route)
        }
        viewModel.resetUiState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            label = {
                Text(text = "Name")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Person, contentDescription = "email")
            },
            value = name,
            placeholder = {
                Text(text = "Email")
            },
            onValueChange = {
                name = it
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = {
                Text(text = "Email")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = "email")
            },
            value = email,
            placeholder = {
                Text(text = "Email")
            },
            onValueChange = {
                email = it
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = {
                Text(text = "Company")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Star, contentDescription = "email")
            },
            value = company,
            placeholder = {
                Text(text = "Company Name")
            },
            onValueChange = {
                company = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = {
                Text(text = "Password")
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "password")
            },
            value = password,
            placeholder = {
                Text(text = "Password")
            },
            onValueChange = {
                password = it
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {
            if (email.isNotEmpty() && password.isNotEmpty()) {
                val employee = Employee(name = name, email =  email, password = password, company = company, type = "admin")
                viewModel?.signUp(employee, context)
            }
        }) {
            Text(text = "Sign Up")
        }

        if (uiState?.value?.loading == true) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .pointerInput(Unit) {}
            ){
                CircularProgressIndicator(
                    modifier = Modifier
                        .width(64.dp)
                        .align(Alignment.Center)
                        .padding(10.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    strokeCap = StrokeCap.Round
                )
            }
        }
        if (uiState?.value?.error?.isNotEmpty() == true){
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = uiState.value.error?: "error signing up",
                color = Color.Red
            )
        }
    }

}

@Preview(showBackground = true)
@Composable
fun Preview(){
    SignUpScreen()
}