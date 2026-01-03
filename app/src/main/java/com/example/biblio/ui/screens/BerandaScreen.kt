package com.example.biblio.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import com.example.biblio.ui.components.Profile
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BookViewModel
import com.example.biblio.viewmodel.BookViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BerandaScreen(
    navController: NavController,
    bottomPadding: Dp,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            bookRepository = FirebaseBookRepository(),
            favoriteRepository = FirebaseFavoriteRepository(
                auth = FirebaseAuth.getInstance(),
                firestore = FirebaseFirestore.getInstance()
            )
        )
    )
) {
    val sections by viewModel.sections.collectAsState()
    val sectionBooks by viewModel.sectionBooks.collectAsState()
    val loadingSections by viewModel.loadingSections.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val state = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.loadSections(forceRefresh = true) }, // ← ganti jadi loadSections
        state = state,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.colorBackground),
                            colorResource(id = R.color.colorBackgroundVariant)
                        )
                    )
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = bottomPadding, top = 10.dp)
            ) {
                item {
                    val user = Firebase.auth.currentUser
                    user?.let {
                        Profile(
                            name = it.displayName ?: "Unknown",
                            photoUrl = it.photoUrl.toString(),
                            modifier = Modifier.padding(horizontal = 16.dp),
                            navController = navController
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // ✅ Ganti logic
                items(
                    items = sections,
                    key = { it.id }
                ) { section ->
                    // ✅ Lazy load books untuk section ini
                    LaunchedEffect(section.id) {
                        viewModel.loadSectionBooks(section.id, section.bookIds)
                    }

                    val books = sectionBooks[section.id] ?: emptyList()
                    val isLoadingSection = loadingSections.contains(section.id)

                    SectionItem(
                        section = section,
                        books = books,
                        isLoading = isLoadingSection,
                        navController = navController,
                        viewModel = viewModel,
                        sharedTransitionScope = sharedTransitionScope,
                        animatedContentScope = animatedContentScope
                    )
                }
            }
        }
    }
}