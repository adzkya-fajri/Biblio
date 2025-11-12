package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblio.R
import com.example.biblio.data.model.Section
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.fraunces
import com.example.biblio.ui.components.*
import com.example.biblio.ui.components.SearchBar
//import com.example.biblio.ui.components.SearchHeader
import com.example.biblio.ui.components.CategoryChips
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory

@Composable
fun CariScreen(
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory (
            BukuRepository(LocalContext.current)
        )
    )
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val bookDatabase by viewModel.bookDatabase.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Ambil semua buku dari semua section
    val allBooks = remember(bookDatabase) {
        bookDatabase?.sections?.flatMap { it.books } ?: emptyList()
    }

    // Filter berdasarkan search query (kategori hanya dekorasi)
    val filteredBooks = remember(searchQuery, allBooks) {
        if (searchQuery.isEmpty()) {
            allBooks
        } else {
            allBooks.filter { book ->
                book.judul.contains(searchQuery, ignoreCase = true) ||
                        book.penulis.contains(searchQuery, ignoreCase = true) ||
                        book.isbn.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // Section populer (take 6)
    val bigSection = remember(filteredBooks) {
        if (filteredBooks.isNotEmpty()) {
            listOf(Section(id = 0, title = "Populer", books = filteredBooks.take(6)))
        } else {
            emptyList()
        }
    }

    // Sections berdasarkan data asli (tapi filtered)
    val sections = remember(filteredBooks, bookDatabase) {
        bookDatabase?.sections?.mapNotNull { section ->
            val sectionBooks = section.books.filter { book ->
                filteredBooks.contains(book)
            }
            if (sectionBooks.isNotEmpty()) {
                section.copy(books = sectionBooks.take(5))
            } else {
                null
            }
        } ?: emptyList()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
//        item {
//            SearchHeader(modifier = Modifier.padding(horizontal = 16.dp))
//        }

        // SEARCH BAR
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // CATEGORY CHIPS (Dekorasi saja)
        item {
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        // LOADING STATE
        if (isLoading) {
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
        // KONDISI: Belum ada input DAN kategori "Semua"
        else if (searchQuery.isEmpty() && selectedCategory == "Semua") {
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
                            // fontFamily = fraunces,  // Uncomment kalau ada custom font
                            color = colorResource(id = R.color.colorOnBackground)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Mulai ketik atau pilih kategori",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        // KONDISI: Ada input TAPI tidak ada hasil
        else if (filteredBooks.isEmpty()) {
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
                            "Coba kata kunci atau kategori lain",
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.colorOnBackground).copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }
        // KONDISI: Ada hasil pencarian - TAMPILKAN BUKU
        else {
            // SECTION POPULER (GRID) - kalau kamu ada BigSectionItem
            if (bigSection.isNotEmpty()) {
                items(
                    items = bigSection,
                    key = { section -> section.id }
                ) { section ->
                    BigSectionItem(section = section)
                }
            }

            // SECTION LAINNYA (HORIZONTAL)
            items(
                items = sections,
                key = { section -> section.id }
            ) { section ->
                SectionItem(section = section)
            }
        }
    }
}