package com.example.biblio.ui.screens

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblio.R
import com.example.biblio.ui.components.*
import com.example.biblio.ui.components.SearchBar
//import com.example.biblio.ui.components.SearchHeader
import com.example.biblio.ui.components.CategoryChips
import com.example.biblio.viewmodel.BookViewModel
import com.example.biblio.viewmodel.BookViewModelFactory
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.data.model.Book
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun CariScreen(
    navController: NavController? = null,
    bottomPadding: Dp,
    viewModel: BookViewModel = viewModel(
        factory = BookViewModelFactory(
            bookRepository = FirebaseBookRepository(),
            favoriteRepository = FirebaseFavoriteRepository(
                auth = FirebaseAuth.getInstance(),
                firestore = FirebaseFirestore.getInstance()
            )
        )
    ),
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    // Trigger search dengan debounce dari ViewModel
    LaunchedEffect(searchQuery) {
        viewModel.searchBooks(searchQuery)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = bottomPadding, top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Header
        item {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    "Mau nyari apa ya?",
                    style = MaterialTheme.typography.headlineLarge,
                    color = colorResource(id = R.color.colorOnBackground)
                )
            }
        }

        // Search Bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Loading State
        if (isSearching) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        // Empty State - belum search
        else if (searchQuery.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_24px),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = colorResource(id = R.color.colorPrimary)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Cari buku favoritmu",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.colorOnBackground)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Mulai ketik judul, penulis, atau ISBN",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        // No Results
        else if (searchResults.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.search_24px),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = colorResource(id = R.color.colorPrimary).copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Buku tidak ditemukan",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = colorResource(id = R.color.colorOnBackground)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Coba kata kunci lain",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        // Results
        else {
            items(
                items = searchResults,
                key = { it.id }
            ) { book ->
                SearchBookCard(
                    book = book,
                    onClick = {
                        navController?.navigate("detail/${book.id}")
                    },
                    sharedTransitionScope = sharedTransitionScope,
                    animatedContentScope = animatedContentScope
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SearchBookCard(
    book: Book,
    onClick: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    with(sharedTransitionScope) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable(onClick = onClick),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = book.cover,
                    contentDescription = book.title,
                    modifier = Modifier
                        .size(60.dp, 90.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .sharedElement(
                            state = rememberSharedContentState(key = "book-${book.id}"),
                            animatedVisibilityScope = animatedContentScope
                        ),
                    contentScale = ContentScale.Crop
                )

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = colorResource(id = R.color.colorOnBackground)
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = book.author,
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.7f)
                    )

                    if (book.isbn.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "ISBN: ${book.isbn}",
                            fontSize = 12.sp,
                            color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}