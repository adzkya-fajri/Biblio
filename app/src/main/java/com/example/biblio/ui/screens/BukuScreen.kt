package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun BukuScreen(
    bookId: String?,
    navController: NavController,
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory(
            BukuRepository(LocalContext.current)
        )
    )
) {
    val bookDatabase by viewModel.bookDatabase.collectAsState()

    // State untuk timeout dan loading
    var isTimeout by remember { mutableStateOf(false) }

    // Jalankan timer 5 detik untuk timeout
    LaunchedEffect(bookId, bookDatabase) {
        if (bookDatabase == null) {
            delay(5000)
            isTimeout = true
        }
    }

    // Cari buku berdasarkan ID
    val book = remember(bookId, bookDatabase) {
        bookDatabase?.sections
            ?.flatMap { it.books }
            ?.find { it.id == bookId }
    }

    when {
        // 🔄 Loading (belum timeout dan book masih null)
        book == null && !isTimeout -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // ❌ Timeout (data gak ketemu setelah 5 detik)
        book == null && isTimeout -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Buku tidak ditemukan")
            }
        }

        // ✅ Data ditemukan
        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Back button
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }

                // Cover buku
                AsyncImage(
                    model = book?.cover,
                    contentDescription = book?.judul,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Info buku
                Text(
                    text = book?.judul ?: "",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = book?.penulis ?: "",
                    fontSize = 18.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ISBN: ${book?.isbn ?: "-"}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
