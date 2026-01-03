package com.example.biblio.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexmono
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.BookContent
import com.example.biblio.viewmodel.BookViewModel
import com.example.biblio.viewmodel.BookViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BukuScreen(
    bookId: String?,
    sectionId: String?,
    bottomPadding: Dp,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    coverHeight: Dp = 225.dp,
    coverWidth: Dp = 150.dp,
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
    val book by viewModel.selectedBook.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()

    var isTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        bookId?.let { viewModel.loadBook(it) }

        isTimeout = false
        delay(5_000)
        if (book == null) isTimeout = true
    }

    when {
        book != null -> {
            BookContent(
                book = book!!,
                navController = navController,
                bottomPadding = bottomPadding,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                coverHeight = coverHeight,
                coverWidth = coverWidth,
                onToggleFavorite = { viewModel.toggleFavorite(it) },
                sectionId = sectionId,
            )
        }

        !isTimeout -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Buku tidak ditemukan")
            }
        }
    }
}