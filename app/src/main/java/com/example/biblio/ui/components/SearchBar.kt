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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Cari di Biblio...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search")
        },
        shape = RoundedCornerShape(50),
        colors = OutlinedTextFieldDefaults.colors(
            // CARA BARU untuk Material3
            focusedContainerColor = colorResource(R.color.colorSurfaceVariant),
            unfocusedContainerColor = colorResource(R.color.colorSurfaceVariant),
            disabledContainerColor = colorResource(R.color.colorSurfaceVariant),

            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,

            cursorColor = colorResource(R.color.colorPrimaryVariant),
        ),
        singleLine = true
    )
}