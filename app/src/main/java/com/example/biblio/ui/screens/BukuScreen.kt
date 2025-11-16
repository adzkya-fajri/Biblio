package com.example.biblio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexmono
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.Profile
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun BukuScreen(
    bookId: String?,
    bottomPadding: Dp,
    navController: NavController,
    coverHeight: Dp = 225.dp,
    coverWidth: Dp = 150.dp,
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory(
            BukuRepository(LocalContext.current),
            FavoriteRepository(LocalContext.current)
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
            ?.find { it.id.toString() == bookId }
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
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                colorResource(id = com.example.biblio.R.color.colorBackground),
                                colorResource(id = R.color.colorBackgroundVariant)
                            )
                        )
                    )
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(bottom = bottomPadding, top = 10.dp)
                ) {
                    item {
                        // Back button
                        Row(modifier = Modifier.fillMaxWidth()) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        // Cover
                        Card(
                            modifier = Modifier
                                .height(coverHeight)
                                .width(coverWidth),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(3.dp)
                        ) {
                            AsyncImage(
                                model = book?.cover,
                                contentDescription = "Book Cover",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        // Info buku
                        Text(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            color = colorResource(id = R.color.colorOnBackground),
                            text = book?.judul ?: "",
                            fontSize = 24.sp,
                            fontFamily = fraunces,
                            textAlign = TextAlign.Center
                        )
                    }

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    item {
                        Text(
                            text = book?.penulis ?: "",
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                            fontSize = 14.sp,
                            fontFamily = ibmplexsans,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }

                    item {
                        Text(
                            text = "ISBN ${book?.isbn ?: "-"}",
                            fontFamily = ibmplexmono,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        // Rating, Halaman, Durasi
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            // Ulasan
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.star_24px),
                                    contentDescription = null,
                                    tint = colorResource(R.color.colorPrimaryVariant),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Ulasan", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                                    Text("4.5", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Halaman
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.book_24px),
                                    contentDescription = null,
                                    tint = colorResource(R.color.colorPrimaryVariant),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Halaman", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                                    Text("350", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Durasi
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.timer_24px),
                                    contentDescription = null,
                                    tint = colorResource(R.color.colorPrimaryVariant),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("Durasi", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                                    Text("5j 30m*", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        // Deskripsi
                        Text(
                            color = colorResource(id = R.color.colorOnBackground),
                            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut eleifend semper fringilla. Vestibulum convallis rutrum arcu, et dapibus arcu sollicitudin eu. Duis gravida faucibus maximus.",
                            fontSize = 14.sp,
                            lineHeight = 1.5.em,
                            fontFamily = ibmplexsans,
                            textAlign = TextAlign.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    item {
                        // Tombol
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.book_5_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Beli & Baca", fontFamily = ibmplexsans,)
                            }

                            OutlinedButton(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.newsstand_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tambahkan ke Koleksi", fontFamily = ibmplexsans)
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

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
}
