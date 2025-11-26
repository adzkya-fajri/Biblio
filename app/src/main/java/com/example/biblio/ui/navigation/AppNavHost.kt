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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biblio.ui.MainScreen
import com.example.biblio.ui.auth.LoginScreen
import com.example.biblio.ui.auth.RegisterScreen
import com.example.biblio.ui.auth.WelcomeScreen
import com.example.biblio.ui.screens.AboutBiblioScreen
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.BookReaderScreen
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
    val navController = rememberNavController()

    // Lemparkan user ke welcome kalau belum login
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
        composable("welcome") {
            WelcomeScreen(navController = navController)
        }
        composable("main") {
            MainScreen(navController = navController)
        }
        composable(
            route = "reader/{bookJson}",
            arguments = listOf(
                navArgument("bookJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookJson = backStackEntry.arguments?.getString("bookJson") ?: ""
            BookReaderScreen(
                bookJson = bookJson,
                navController = navController
            )
        }
        composable("style_text") {
            StyleTextScreen(onNavigateBack = { navController.navigateUp() })
        }
    }
}