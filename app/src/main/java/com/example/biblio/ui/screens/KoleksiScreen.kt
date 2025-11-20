package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.biblio.data.model.Buku
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import com.example.biblio.fraunces
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KoleksiScreen(
    bottomPadding: Dp,
    onBookClick: (String) -> Unit,
    viewModel: BukuViewModel = viewModel(          // <— tambahkan factory
        factory = BukuViewModelFactory(
            BukuRepository(LocalContext.current),
            FavoriteRepository(LocalContext.current)
        )
    )
) {
    val list by viewModel.favoriteBooks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Koleksi Favorit", fontFamily = fraunces)
                },
                actions = {
                    if (list.isNotEmpty()) {
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
            if (list.isEmpty()) {
                EmptyKoleksi()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list, key = { it.id }) { buku ->
                        KoleksiCard(buku, onBookClick = { onBookClick(buku.id) })
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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun KoleksiCard(buku: Buku, onBookClick: () -> Unit) {
    Card(
        onClick = onBookClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = buku.cover,
                contentDescription = buku.judul,
                modifier = Modifier
                    .width(60.dp)
                    .aspectRatio(2f / 3f),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(buku.judul, fontWeight = FontWeight.SemiBold)
                Text(buku.penulis, style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = { /* toggle langsung di detail */ }) {
                Icon(Icons.Default.Favorite, contentDescription = "Favorit")
            }
        }
    }
}