package com.example.biblio.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun CategoryChips(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Semua", "Fiksi", "Motivasi", "Non-fiksi",
        "Biografi", "Sains", "Indonesia"
    )
    val colors = listOf(
        Color(0xFF2196F3), Color(0xFFE53935), Color(0xFF00C853),
        Color(0xFFFFB74D), Color(0xFFFFCDD2), Color(0xFFCE93D8),
        Color(0xFF81C784)
    )

    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories.size) { index ->
            val category = categories[index]
            val isSelected = selectedCategory == category

            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = { Text(category) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = colors[index % colors.size],
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFFF5F5F5)
                )
            )
        }
    }
}