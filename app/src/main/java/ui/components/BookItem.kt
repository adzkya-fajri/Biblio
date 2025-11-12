package com.example.biblio.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.ibmplexsans
import com.example.biblio.data.model.Buku

@Composable
fun BookItem(
    book: Buku,
    coverHeight: Dp = 180.dp,
    coverWidth: Dp = 120.dp
) {
    Column(
        modifier = Modifier.width(coverWidth)
    ) {
        // Card untuk cover
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverHeight),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            AsyncImage(
                model = book.cover,
                contentDescription = "Book Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Judul buku
        Text(
            text = book.judul,
            lineHeight = 1.25.em,
            fontSize = 14.sp,
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Normal,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Penulis
        Text(
            text = book.penulis,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            fontFamily = ibmplexsans,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )
    }
}