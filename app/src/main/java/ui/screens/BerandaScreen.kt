package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.biblio.ui.components.BigSectionItem
import com.example.biblio.Book
import com.example.biblio.ui.components.Profile
import com.example.biblio.R
import com.example.biblio.Section
import com.example.biblio.ui.components.SectionItem

@Composable @Preview
fun BerandaScreen() {
    val bigSection = remember {
        val sampleBooks = List(5) { Book(R.drawable.sample_cover) }
        listOf(
            Section("Udah ngopi belum Andi?", sampleBooks),
        )
    }

    val sections = remember {
        val sampleBooks = List(5) { Book(R.drawable.sample_cover) }
        listOf(
            Section("Novel", sampleBooks),
            Section("Sains & Teknologi", sampleBooks),
            Section("Bisnis", sampleBooks),
            Section("Ini Buku kamu banget!", sampleBooks)
        )
    }

    // LazyColumn = RecyclerView vertikal
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 24.dp  // Margin bawah
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        item {
            Profile(name = "Andi",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        items(bigSection) { bigSection ->
            BigSectionItem(section = bigSection)
        }

        items(sections) { section ->
            SectionItem(section = section)
        }
    }
}