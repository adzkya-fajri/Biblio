package com.example.biblio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.biblio.data.model.Section
import com.example.biblio.fraunces
import com.example.biblio.viewmodel.BukuViewModel

@Composable
fun BigSectionItem(
    section: Section,
    navController: NavController? = null,
    viewModel: BukuViewModel
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = section.title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fraunces,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(section.books) { book ->
                BookItem(book = book, viewModel = viewModel, coverHeight = 225.dp, coverWidth = 150.dp)
            }
        }
    }
}