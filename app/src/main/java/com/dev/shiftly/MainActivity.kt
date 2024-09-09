package com.dev.shiftly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.dev.shiftly.data.data_source.Employee
import com.dev.shiftly.navigation.Navigation
import com.dev.shiftly.navigation.Screen
import com.dev.shiftly.screens.admin.viewmodels.AdminViewModel
import com.dev.shiftly.ui.theme.ShiftlyTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShiftlyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.White
            ) {
//                DrawerContent(navController = navController, drawerState)
            }
        },
        gesturesEnabled = drawerState.isOpen
    ) {
        MainScaffold(
            drawerState = drawerState,
            coroutineScope = coroutineScope,
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    navController: NavHostController
) {
//    val currentDestination = navController.currentBackStackEntryFlow.collectAsState(initial = navController.currentBackStackEntry)
    val title = remember { mutableStateOf("Shiftly") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title.value,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {

                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher),
                        contentDescription = "drawer icon",
                        modifier = Modifier.size(40.dp)
                    )

                },
                actions = {
                    IconButton(onClick = {
                        val user =
                            SharedPrefsHelper.getInstance(navController.context).getString("user")
                        val currentUser = Gson().fromJson(user, Employee::class.java)
                        navController.navigate(Screen.Profile.withArgs(currentUser.id))
                    }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = {
                        FirebaseAuth
                            .getInstance()
                            .signOut()
                        navController.navigate(Screen.SignIn.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "sign out",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = Color.White,
                )
            )
        }
    ) {
        Navigation(it, navController, title)
    }
}

@Composable
fun DrawerContent(
    navController: NavHostController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    var currentUser by remember { mutableStateOf(Employee()) }
    val context = LocalContext.current
    var isChecked by remember { mutableStateOf(false) }
    val adminViewModel: AdminViewModel = hiltViewModel()
    LaunchedEffect(key1 = Unit) {
        val user = SharedPrefsHelper.getInstance(context).getString("user")
        currentUser = Gson().fromJson(user, Employee::class.java)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(0.4f)
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 16.dp)
    ) {
        Text(
            text = currentUser.name,
            color = Color.Black,
            fontStyle = MaterialTheme.typography.headlineMedium.fontStyle,
        )
        if (currentUser.type != "admin") {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = currentUser.position,
                color = Color.Black,
                fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = "Are you available?",
                    color = Color.Black,
                    fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isChecked,
                    onCheckedChange = {
                        isChecked = it
                        currentUser.isAvailable = it
                        adminViewModel.updateEmployee(currentUser)
                    }
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    FirebaseAuth
                        .getInstance()
                        .signOut()
                    navController.navigate(Screen.SignIn.route)
                    scope.launch {
                        drawerState.close()
                    }
                },
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Sign Out",
                color = Color.Black,
                fontStyle = MaterialTheme.typography.headlineSmall.fontStyle,
            )
            Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "sign out")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShiftlyTheme {
        MainScreen()
    }
}
