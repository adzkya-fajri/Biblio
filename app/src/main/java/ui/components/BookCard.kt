package com.example.biblio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.Section
@Composable
fun BookCard(
    book: com.example.biblio.Book,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { /* Handle click */ }
    ) {
        // COVER BUKU
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Image(
                painter = painterResource(id = book.coverResId),
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // JUDUL BUKU
        if (book.title.isNotEmpty()) {
            Text(
                text = book.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                lineHeight = 18.sp
            )
        }

        // AUTHOR
        if (book.author.isNotEmpty()) {
            Text(
                text = book.author,
                fontSize = 12.sp,
                color = Color.Gray,
                maxLines = 1
            )
        }
    }
}