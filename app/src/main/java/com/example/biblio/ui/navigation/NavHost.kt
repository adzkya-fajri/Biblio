package com.example.biblio.ui.navigation

import android.util.Log
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biblio.ui.MainScreen
import com.example.biblio.ui.screens.KoleksiScreen
import com.example.biblio.ui.auth.LoginScreen
import com.example.biblio.ui.auth.WelcomeScreen
import com.example.biblio.viewmodel.AuthState
import com.example.biblio.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var auth: FirebaseAuth

@Composable
fun NavHost() {
    val navController = rememberNavController()

    // Initialize Firebase Auth
    auth = Firebase.auth
    val currentUser = auth.currentUser

    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()

    // Observe auth state changes
    LaunchedEffect(authState) {
        Log.d("NavGraph", "AuthState changed: $authState")
        Log.d("NavGraph", "Current user: ${Firebase.auth.currentUser}")

        when (authState) {
            is AuthState.Success -> {
                Log.d("NavGraph", "Navigate to main")
                navController.navigate("main") {
                    popUpTo(0) { inclusive = true }
                }
            }
            is AuthState.Idle -> {
                if (Firebase.auth.currentUser == null) {
                    Log.d("NavGraph", "Navigate to login")
                    navController.navigate("welcome") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }

    val startDestination = if (Firebase.auth.currentUser != null) "main" else "welcome"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it / 2 },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            )
        },

        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { -it / 3 },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutLinearInEasing
                )
            )
        },

        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it / 3 },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutSlowInEasing
                )
            ) + fadeIn(tween(400, easing = FastOutSlowInEasing))
        },

        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { it / 3 },
                animationSpec = tween(
                    durationMillis = 200,
                    easing = FastOutLinearInEasing
                )
            ) + fadeOut(tween(100, easing = FastOutLinearInEasing))
        }
    ) {
        composable("login") {
            LoginScreen(onLoginSuccess = { navController.navigate("main") })
        }
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable("koleksi") {
            KoleksiScreen()
        }
    }
}