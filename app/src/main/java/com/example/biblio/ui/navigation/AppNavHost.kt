package com.example.biblio.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biblio.ui.MainScreen
import com.example.biblio.ui.auth.LoginScreen
import com.example.biblio.ui.auth.RegisterScreen
import com.example.biblio.ui.auth.WelcomeScreen
import com.example.biblio.ui.screens.AboutBiblioScreen
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.EditNameScreen
import com.example.biblio.ui.screens.KoleksiScreen
import com.example.biblio.ui.screens.ManageProfileScreen
import com.example.biblio.ui.screens.ProfileScreen
import com.example.biblio.ui.screens.SettingsScreen
import com.example.biblio.ui.screens.StyleTextScreen
import com.example.biblio.viewmodel.AuthState
import com.example.biblio.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth


@Composable
fun AppNavHost() {
fun AppNavigation() { // ✅ PASTIKAN NAMA INI
    val navController = rememberNavController()

    // Lemparkan user ke welcome kalau belum login
    val startDestination = if (Firebase.auth.currentUser != null) "main" else "welcome"
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
                onNavigateToProfile = {
                    println("DEBUG: Navigating to profile screen")
                    navController.navigate("profile")
                },
                navController = navController
            )
        }

        // ===== PROFILE ROUTES (TAMBAHAN BARU) =====
        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToEditName = {
                    navController.navigate("editName")
                },
                onNavigateToManageProfile = {
                    navController.navigate("manageProfile")
                },
                onNavigateToStyleText = {
                    navController.navigate("styleText")
                },
                onNavigateToAbout = {
                    navController.navigate("about")
                }
            )
        }

        composable("editName") {
            EditNameScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("manageProfile") {
            ManageProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("styleText") {
            StyleTextScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("about") {
            AboutBiblioScreen(
                onNavigateBack = { navController.popBackStack() }
            )
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                navController = navController
            )
        }
        composable("register"){
            RegisterScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("welcome") { inclusive = true }
                    }
                },
                navController = navController
            )
        }

        composable("settings") {
            SettingsScreen(
                onLogoutSuccess = {
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}