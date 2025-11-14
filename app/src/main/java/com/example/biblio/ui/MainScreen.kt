package com.example.biblio.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BottomBar
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.BukuScreen
import com.example.biblio.ui.screens.CariScreen
import com.example.biblio.ui.screens.KoleksiScreen
import com.example.biblio.ui.screens.SettingsScreen
import com.example.biblio.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val tabs = listOf("beranda", "cari", "koleksi")

    var selectedTab by remember { mutableIntStateOf(0) }
    var previousTab by remember { mutableIntStateOf(0) }

    // Ini biar si user engga nyasar ke beranda kalau belum login
    val user = Firebase.auth.currentUser
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("welcome") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    val transitionSpec: AnimatedContentTransitionScope<Int>.() -> ContentTransform = {
        if (targetState > initialState) {
            // Navigasi ke kanan → geser dari kanan ke kiri
            slideInHorizontally { it } + fadeIn() togetherWith
                    slideOutHorizontally { -it } + fadeOut()
        } else {
            // Navigasi ke kiri → geser dari kiri ke kanan
            slideInHorizontally { -it } + fadeIn() togetherWith
                    slideOutHorizontally { it } + fadeOut()
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { index ->
                    selectedTab = index
                    innerNavController.navigate(tabs[index]) {
                        popUpTo(tabs.first()) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                fontFamily = ibmplexsans
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = innerNavController,
            startDestination = "beranda",
            modifier = Modifier.padding(paddingValues),
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
            composable("beranda") { BerandaScreen(navController = innerNavController) }
            composable("cari") { CariScreen() }
            composable("koleksi") { KoleksiScreen() }

            composable(
                route = "buku/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BukuScreen(
                    bookId = bookId,
                    navController = navController
                )
            }
            composable("settings") { SettingsScreen(navController = navController) }
        }
    }
}
