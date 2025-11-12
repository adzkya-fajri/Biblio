package com.example.biblio.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.Section
import com.example.biblio.fraunces

@Composable
fun BigSectionItem(section: Section) {
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

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                section.books.chunked(3).forEach { rowBooks ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowBooks.forEach { book ->
                            BookCard(
                                book = book,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Tambah spacer jika tidak penuh 3 kolom
                        repeat(3 - rowBooks.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
    }
}