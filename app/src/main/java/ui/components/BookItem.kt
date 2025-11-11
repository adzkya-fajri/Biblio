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
import com.example.biblio.Book
import com.example.biblio.R
import com.example.biblio.ibmplexsans

@Composable
fun BookItem(book: Book, coverHeight: Dp = 180.dp, coverWidth: Dp = 120.dp) {
    Column(
        modifier = Modifier.width(coverWidth)
    ) {
        // Card untuk cover
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverHeight),
            shape = RoundedCornerShape(0.dp),
            elevation = CardDefaults.cardElevation(3.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_cover),
                contentDescription = "Book Cover",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Judul buku
        Text(
            text = "Buku Keren dan Asik",
            fontFamily = ibmplexsans,
            lineHeight = 1.25.em,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            maxLines = 2,  // Maksimal 2 baris
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Penulis
        Text(
            text = "Penulis Keren",
            fontFamily = ibmplexsans,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray,
            maxLines = 1,  // Maksimal 1 baris
            modifier = Modifier.fillMaxWidth()
        )
    }
}