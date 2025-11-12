package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.Book
import com.example.biblio.R
import com.example.biblio.Section
import com.example.biblio.fraunces
import com.example.biblio.ui.components.*
import com.example.biblio.ui.components.SearchBar
import com.example.biblio.ui.components.SearchHeader
import com.example.biblio.ui.components.CategoryChips

@Composable
fun CariScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Semua") }

    val allBooks = remember {
        listOf(
            Book(R.drawable.sample_cover, "The Subtle Art", "Mark Manson", "Motivasi"),
            Book(R.drawable.sample_cover, "Jasad Heiir", "Nadya Hashem", "Fiksi"),
            Book(R.drawable.sample_cover, "Sapiens", "Yuval Noah Harari", "Non-fiksi"),
            Book(R.drawable.sample_cover, "Atomic Habits", "James Clear", "Motivasi"),
            Book(R.drawable.sample_cover, "The Alchemist", "Paulo Coelho", "Fiksi"),
            Book(R.drawable.sample_cover, "Becoming", "Michelle Obama", "Biografi"),
            Book(R.drawable.sample_cover, "Educated", "Tara Westover", "Non-fiksi"),
            Book(R.drawable.sample_cover, "1984", "George Orwell", "Fiksi"),
        )
    }

    val filteredBooks = remember(searchQuery, selectedCategory, allBooks) {
        allBooks.filter { book ->
            val matchesSearch = searchQuery.isEmpty() ||
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "Semua" ||
                    book.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    val bigSection = remember(filteredBooks) {
        listOf(Section("📈 Populer", filteredBooks.take(6)))
    }

    val sections = remember(filteredBooks) {
        listOf(
            Section("Buku ini kamu banget!", filteredBooks.take(5)),
            Section("Non-fiksi", filteredBooks.filter { it.category == "Non-fiksi" }),
            Section("Novel Fiksi", filteredBooks.filter { it.category == "Fiksi" }),
            Section("Motivasi & Self-Help", filteredBooks.filter { it.category == "Motivasi" })
        ).filter { it.books.isNotEmpty() }
    }

    // PERBAIKAN: LazyColumn SELALU TAMPIL, placeholder di dalam item
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // HEADER
        item {
            SearchHeader(modifier = Modifier.padding(horizontal = 16.dp))
        }

        // SEARCH BAR - SELALU TAMPIL
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // CATEGORY CHIPS - SELALU TAMPIL
        item {
            CategoryChips(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it }
            )
        }

        // KONDISI: Belum ada input DAN kategori "Semua"
        if (searchQuery.isEmpty() && selectedCategory == "Semua") {
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
                            fontFamily = fraunces,
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
            // SECTION POPULER (GRID)
            items(bigSection) { section ->
                BigSectionItem(section = section)
            }

            // SECTION LAINNYA (HORIZONTAL)
            items(sections) { section ->
                SectionItem(section = section)
            }
        }
    }
}