package com.example.biblio.ui.screens

import android.R.attr.name
import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
//import com.example.biblio.data.repository.UserRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BigSectionItem
import com.example.biblio.ui.components.Profile
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BerandaScreen(
    onNavigateToProfile: () -> Unit,
    navController: NavController,
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory(
            BukuRepository(LocalContext.current),
            FavoriteRepository(LocalContext.current)
        )
    ),
//    userViewModel: UserViewModel = viewModel(
//        factory = UserViewModelFactory(
//            UserRepository(LocalContext.current)
//        )
//    ) // ✅ INJECT UserViewModel
) {
    val bookDatabase by viewModel.bookDatabase.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val state = rememberPullToRefreshState()

    // ✅ GET USER DATA
//    val userProfile by userViewModel.userProfile.collectAsState()
//    val displayName = userProfile.name
//    val photoUrl = userProfile.photoUrl

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
                contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
//                item {
//                    Profile(
//                        name = displayName,
//                        photoUrl = photoUrl, // ✅ PASS DATA
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        onProfileClick = onNavigateToProfile
//                    )
//                }

                val sections = bookDatabase?.sections ?: emptyList()
                if (sections.isNotEmpty()) {
                    items(
                        items = sections,
                        key = { section -> section.id }
                    ) { section ->
                        SectionItem(
                            section = section,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}