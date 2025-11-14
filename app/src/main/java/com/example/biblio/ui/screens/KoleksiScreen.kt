package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.R
import com.example.biblio.fraunces

@Composable @Preview
fun KoleksiScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),  // ambil seluruh layar
        contentAlignment = Alignment.Center  // semua content di tengah
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.newsstand_24px),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                tint = colorResource(id = R.color.colorPrimary)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Koleksi",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fraunces,
                color = colorResource(id = R.color.colorOnBackground)
            )
        }
    }
}