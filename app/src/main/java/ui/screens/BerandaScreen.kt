package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblio.ui.components.BigSectionItem
import com.example.biblio.ui.components.Profile
import com.example.biblio.R
import com.example.biblio.data.model.Buku
import com.example.biblio.data.model.Section
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory

@Composable
@Preview
fun BerandaScreen(
    viewModel: BukuViewModel = viewModel(
        factory = BukuViewModelFactory (
            BukuRepository(LocalContext.current)
        )
    )
) {
    val bookDatabase by viewModel.bookDatabase.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // LazyColumn = RecyclerView vertikal
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 16.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Profile(
                name = "Andi",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        // Loading state
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // Section items dari database
        bookDatabase?.sections?.let { sections ->
            items(
                items = sections,
                key = { section -> section.id }
            ) { section ->
                SectionItem(section = section)  // ← Pakai SectionItem yang udah dibuat
            }
        }
    }
}