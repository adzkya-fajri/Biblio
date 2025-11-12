package com.example.biblio.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// KOMPONEN HEADER SEARCH
@Composable
fun SearchHeader(modifier: Modifier = Modifier) {
    Text(
        text = "Mau nyari apa ya?",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}