package com.example.biblio.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
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
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import com.example.biblio.ui.components.Profile
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun BerandaScreen(
    navController: NavController,
    bottomPadding: Dp,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory(
            BukuRepository(LocalContext.current),
            FavoriteRepository(LocalContext.current)
        )
    ),
) {
    val bookDatabase by viewModel.bookDatabase.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val state = rememberPullToRefreshState()
    // Di BerandaScreen
    val sections = bookDatabase?.sections?.take(1) ?: emptyList() // Load 1 section dulu

    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = { viewModel.loadBooks(forceRefresh = true) },
        state = state,
        indicator = {
            Indicator(
                modifier = Modifier.align(Alignment.TopCenter),
                isRefreshing = isLoading,
                containerColor = colorResource(R.color.colorSecondary),
                color = colorResource(R.color.colorOnSecondary),
                state = state
            )
        },
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

                val sections = bookDatabase?.sections ?: emptyList()
                if (sections.isNotEmpty()) {
                    items(
                        items = sections,
                        key = { section -> section.id }
                    ) { section ->
                        SectionItem(
                            section = section,
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
}