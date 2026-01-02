package com.example.biblio.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BottomBar
import com.example.biblio.ui.components.NowReadingBar
import com.example.biblio.ui.screens.*
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

// MainScreen.kt - REFACTORED
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val context = LocalContext.current
    val tabs = listOf("beranda", "cari", "koleksi")

    val sharedViewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory(
            BukuRepository(context),
            FavoriteRepository(context)
        )
    )

    // Track current route
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Dynamic UI visibility
    val isReaderScreen = currentRoute == "reader/{bookJson}"

    val showBottomBar = !isReaderScreen
    val showMiniPlayer = remember(currentRoute) {
        mutableStateOf(!isReaderScreen)
    }

    val bottomPadding by remember {
        derivedStateOf {
            when {
                !showBottomBar -> 0.dp
                showMiniPlayer.value -> 200.dp
                else -> 108.dp
            }
        }
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    val user = Firebase.auth.currentUser
    LaunchedEffect(user) {
        if (user == null) {
            navController.navigate("welcome") {
                popUpTo("main") { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SharedTransitionLayout {
            NavHost(
                navController = innerNavController,
                startDestination = "beranda",
                enterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { it / 2 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
                },
                exitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { -it / 3 },
                        animationSpec = tween(200, easing = FastOutLinearInEasing)
                    ) + fadeOut(animationSpec = tween(100, easing = FastOutLinearInEasing))
                },
                popEnterTransition = {
                    slideInHorizontally(
                        initialOffsetX = { -it / 3 },
                        animationSpec = tween(200, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(400, easing = FastOutSlowInEasing))
                },
                popExitTransition = {
                    slideOutHorizontally(
                        targetOffsetX = { it / 3 },
                        animationSpec = tween(200, easing = FastOutLinearInEasing)
                    ) + fadeOut(tween(100, easing = FastOutLinearInEasing))
                }
            ) {
                composable("beranda") {
                    BerandaScreen(
                        navController = innerNavController,
                        bottomPadding = bottomPadding,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        viewModel = sharedViewModel
                    )
                }

                composable("cari") {
                    CariScreen(
                        bottomPadding = bottomPadding,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        viewModel = sharedViewModel
                    )
                }

                composable("koleksi") {
                    KoleksiScreen(
                        bottomPadding = bottomPadding,
                        onBookClick = { bookId ->
                            innerNavController.navigate("buku/$bookId")
                        },
                        viewModel = sharedViewModel
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
                        navController = innerNavController,
                        bottomPadding = bottomPadding,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable,
                        viewModel = sharedViewModel
                    )
                }

                // READER - Pindah dari AppNavHost
                composable(
                    route = "reader/{bookJson}",
                    arguments = listOf(
                        navArgument("bookJson") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val bookJson = backStackEntry.arguments?.getString("bookJson") ?: ""
                    BookReaderScreen(
                        bookJson = bookJson,
                        navController = innerNavController
                    )
                }

                composable("settings") { SettingsScreen(navController = navController) }
                composable("profile") { ProfileScreen(navController = navController) }
            }
        }

        // Mini player - only show on main tabs
        AnimatedVisibility(
            visible = showMiniPlayer.value && showBottomBar,
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter) // 🔥 THIS is critical
        ) {
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
                onDismiss = { showMiniPlayer.value = false },
            )
        }

        // Bottom bar - only show on main tabs
        AnimatedVisibility(
            visible = showBottomBar,
            enter = slideInVertically(
                initialOffsetY = { it }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ) + fadeOut(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
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
}