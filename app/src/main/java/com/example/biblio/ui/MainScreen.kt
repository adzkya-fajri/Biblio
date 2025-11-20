package com.example.biblio.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BottomBar
import com.example.biblio.ui.components.NowReadingBar
import com.example.biblio.ui.screens.AboutBiblioScreen
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.BukuScreen
import com.example.biblio.ui.screens.CariScreen
import com.example.biblio.ui.screens.EditNameScreen
import com.example.biblio.ui.screens.KoleksiScreen
import com.example.biblio.ui.screens.ManageProfileScreen
import com.example.biblio.ui.screens.ProfileScreen
import com.example.biblio.ui.screens.SettingsScreen
import com.example.biblio.ui.screens.StyleTextScreen
import com.example.biblio.viewmodel.AuthViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun MainScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val tabs = listOf("beranda", "cari", "koleksi")

    var showMiniPlayer by remember { mutableStateOf(true) }
    val bottomPadding by remember {
        derivedStateOf { if (showMiniPlayer) 200.dp else 108.dp }
    }

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

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = innerNavController,
            startDestination = "beranda",
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
            composable("beranda") { BerandaScreen(navController = innerNavController, bottomPadding = bottomPadding) }
            composable("cari") { CariScreen(bottomPadding = bottomPadding) }
            composable("koleksi") {
                KoleksiScreen(
                    bottomPadding = bottomPadding,
                    onBookClick = { bookId ->
                        innerNavController.navigate("buku/$bookId")
                    }
                )
            }

            composable(
                route = "buku/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BukuScreen(
                    bookId = bookId,
                    navController = navController,
                    bottomPadding = bottomPadding
                )
            }
            composable("settings") { SettingsScreen(navController = navController) }
    //            composable("tentang") { AboutBiblioScreen(navController = navController) }
            composable("profile") { ProfileScreen(navController = navController) }
        }

        // Mini player overlay
        if (showMiniPlayer) {
            NowReadingBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp),
                bookTitle = "Laskar Pelangi",
                bookAuthor = "Andrea Hirata",
                bookCover = "https://picsum.photos/43",
                colorContainer = Color(0xFF6B4226),
                currentPage = 265,
                totalPages = 350,
                onContinueReading = { },
                onDismiss = { showMiniPlayer = false }
            )
        }

        // Navbar TERAKHIR (layer depan) - extract dari Scaffold
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
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
    }
}
