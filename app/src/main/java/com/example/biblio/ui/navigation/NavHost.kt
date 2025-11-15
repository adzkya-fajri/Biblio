package com.example.biblio.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.biblio.ui.MainScreen
import com.example.biblio.ui.auth.LoginScreen
import com.example.biblio.ui.auth.WelcomeScreen
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.KoleksiScreen
import com.example.biblio.ui.screens.SettingsScreen
import com.example.biblio.viewmodel.AuthState
import com.example.biblio.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() { // ✅ PASTIKAN NAMA INI
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()

    // Auth state handler
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Idle -> {
                if (auth.currentUser == null) {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    val startDestination = if (auth.currentUser != null) "main" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it / 2 }, animationSpec = tween(300, easing = FastOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it / 3 }, animationSpec = tween(200, easing = FastOutLinearInEasing)) +
                    fadeOut(animationSpec = tween(100, easing = FastOutLinearInEasing))
        }
    ) {
        composable("welcome") { WelcomeScreen(navController = navController) }
        composable("login") { LoginScreen(onLoginSuccess = { navController.navigate("main") }) }
        composable("main") { MainScreen(navController = navController) }
        composable("koleksi") { KoleksiScreen() }

        // ✅ PASTIKAN ADA INI
        composable("beranda") {
            BerandaScreen(
                onNavigateToProfile = { navController.navigate("settings") },
                navController = navController
            )
        }

        composable("settings") {
            SettingsScreen(onLogoutSuccess = {
                navController.navigate("welcome") {
                    popUpTo(0) { inclusive = true }
                }
            })
        }
    }
}