package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.biblio.data.model.Book
import com.example.biblio.data.repository.FirebaseBookRepository
import com.example.biblio.data.repository.FirebaseFavoriteRepository
import com.example.biblio.fraunces
import com.example.biblio.viewmodel.BookViewModel
import com.example.biblio.viewmodel.BookViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoleksiScreen(
    bottomPadding: Dp,
    onBookClick: (String) -> Unit,
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
    val favoriteBooks by viewModel.favoriteBooks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Koleksi Favorit", fontFamily = fraunces)
                },
                actions = {
                    if (favoriteBooks.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearAllFavorites() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus semua")
                        }
                    }
                }
            )
        }
    ) { pad ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(bottom = bottomPadding)
        ) {
            if (favoriteBooks.isEmpty()) {
                EmptyKoleksi()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = favoriteBooks,
                        key = { it.id }
                    ) { book ->
                        KoleksiCard(
                            book = book,
                            onBookClick = { onBookClick(book.id) },
                            onToggleFavorite = { viewModel.toggleFavorite(book.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyKoleksi() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            modifier = Modifier.size(96.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = .38f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Belum ada buku favorit",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tandai buku yang kamu suka",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KoleksiCard(
    book: Book,
    onBookClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Card(
        onClick = onBookClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = book.cover,
                contentDescription = book.title,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                if (book.publisher.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = book.publisher,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    Icons.Default.Favorite,
                    contentDescription = "Remove from favorites",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}