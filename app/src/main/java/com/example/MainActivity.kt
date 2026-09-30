package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.AppRepository
import com.example.model.UserRole
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.driver.DriverHomeScreen
import com.example.ui.passenger.PassengerHomeScreen
import com.example.ui.theme.MotoGoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MotoGoTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val currentRole by AppRepository.currentRole.collectAsState()
    var isAuthScreenVisible by remember { mutableStateOf(false) }

    // Back handling: if in Admin or Driver mode or Auth screen, navigate back to Passenger
    BackHandler(enabled = currentRole != UserRole.PASSENGER || isAuthScreenVisible) {
        if (isAuthScreenVisible) {
            isAuthScreenVisible = false
        } else {
            AppRepository.switchRole(UserRole.PASSENGER)
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = if (isAuthScreenVisible) "auth" else currentRole.name,
            label = "role_navigation"
        ) { target ->
            when (target) {
                "auth" -> {
                    AuthScreen(
                        onLoginSuccess = { role ->
                            AppRepository.switchRole(role)
                            isAuthScreenVisible = false
                        }
                    )
                }

                UserRole.PASSENGER.name -> {
                    PassengerHomeScreen(
                        onSwitchRole = { newRole ->
                            AppRepository.switchRole(newRole)
                        }
                    )
                }

                UserRole.DRIVER.name -> {
                    DriverHomeScreen(
                        onSwitchRole = { newRole ->
                            AppRepository.switchRole(newRole)
                        }
                    )
                }

                UserRole.ADMIN.name -> {
                    AdminDashboardScreen(
                        onSwitchRole = { newRole ->
                            AppRepository.switchRole(newRole)
                        }
                    )
                }

                else -> {
                    PassengerHomeScreen(
                        onSwitchRole = { newRole ->
                            AppRepository.switchRole(newRole)
                        }
                    )
                }
            }
        }
    }
}
