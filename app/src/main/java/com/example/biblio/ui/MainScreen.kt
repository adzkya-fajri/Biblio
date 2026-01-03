package com.example.biblio.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BottomBar
import com.example.biblio.ui.components.NowReadingBar
import com.example.biblio.ui.screens.*
import com.example.biblio.viewmodel.BookViewModel
import com.example.biblio.viewmodel.BookViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

// MainScreen.kt - REFACTORED
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(navController: NavController) {
    val innerNavController = rememberNavController()
    val context = LocalContext.current
    val tabs = listOf("beranda", "cari", "koleksi")

    val sharedViewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            bookRepository = FirebaseBookRepository(),
            favoriteRepository = FirebaseFavoriteRepository(
                auth = FirebaseAuth.getInstance(),
                firestore = FirebaseFirestore.getInstance()
            )
        )
    )

// Track current route
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isReaderScreen = currentRoute == "reader/{bookJson}"

    // Persistent for app lifetime
    val miniPlayerVisible = remember {
        mutableStateOf(true)
    }

    // Auto-hide on reader
    LaunchedEffect(isReaderScreen) {
        if (isReaderScreen) {
            miniPlayerVisible.value = false
        }
    }

    val showBottomBar = !isReaderScreen
    val showMiniPlayer = miniPlayerVisible.value && !isReaderScreen

    val targetPadding by remember(showBottomBar, showMiniPlayer) {
        derivedStateOf {
            when {
                !showBottomBar -> 0.dp
                showMiniPlayer -> 200.dp
                else -> 108.dp
            }
        }
    }

    val bottomPadding by animateDpAsState(
        targetValue = targetPadding,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "BottomPadding"
    )

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
//                enterTransition = {
//                    slideInHorizontally(
//                        initialOffsetX = { it / 2 },
//                        animationSpec = tween(300, easing = FastOutSlowInEasing)
//                    ) + fadeIn(animationSpec = tween(200, easing = FastOutSlowInEasing))
//                },
//                exitTransition = {
//                    slideOutHorizontally(
//                        targetOffsetX = { -it / 3 },
//                        animationSpec = tween(200, easing = FastOutLinearInEasing)
//                    ) + fadeOut(animationSpec = tween(100, easing = FastOutLinearInEasing))
//                },
//                popEnterTransition = {
//                    slideInHorizontally(
//                        initialOffsetX = { -it / 3 },
//                        animationSpec = tween(200, easing = FastOutSlowInEasing)
//                    ) + fadeIn(tween(400, easing = FastOutSlowInEasing))
//                },
//                popExitTransition = {
//                    slideOutHorizontally(
//                        targetOffsetX = { it / 3 },
//                        animationSpec = tween(200, easing = FastOutLinearInEasing)
//                    ) + fadeOut(tween(100, easing = FastOutLinearInEasing))
//                }
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
                    route = "detail/{bookId}?sectionId={sectionId}",
                    arguments = listOf(
                        navArgument("bookId") { type = NavType.StringType },
                        navArgument("sectionId") {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    val sectionId = backStackEntry.arguments?.getString("sectionId")

                    BukuScreen(
                        bookId = bookId,
                        sectionId = sectionId, // ← pass ke screen
                        bottomPadding = bottomPadding,
                        navController = navController,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedContentScope = this@composable
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
//                    BookReaderScreen(
//                        bookJson = bookJson,
//                        navController = innerNavController
//                    )
                }

                composable("settings") { SettingsScreen(navController = navController) }
                composable("profile") { ProfileScreen(navController = navController) }
            }
        }

        // Mini player - only show on main tabs
        AnimatedVisibility(
            visible = miniPlayerVisible.value && showBottomBar,
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
                onDismiss = { miniPlayerVisible.value = false }
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