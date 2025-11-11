package com.example.biblio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.Section
import com.example.biblio.fraunces


@Composable
fun SectionItem(section: Section) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = section.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = fraunces,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal scroll untuk books (LazyRow = RecyclerView horizontal)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

                items(section.books) { book ->
                    BookItem(
                        book = book,
                        coverWidth = 120.dp  // ✅ Benar, sesuai parameter BookItem
                    )
               }
        }
    }
}