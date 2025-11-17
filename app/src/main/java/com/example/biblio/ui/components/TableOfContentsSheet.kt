package com.example.biblio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.R
import com.example.biblio.data.model.Chapter
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableOfContentsSheet(
    chapters: List<Chapter>,
    currentChapterIndex: Int,
    onChapterSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    val backgroundColor = if (isDarkMode) Color(0xFF2A2A2A) else colorResource(R.color.colorBackground)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else colorResource(R.color.colorOnBackground)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Daftar Isi",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fraunces,
                color = textColor,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(chapters) { index, chapter ->
                    ChapterItem(
                        chapter = chapter,
                        isSelected = index == currentChapterIndex,
                        onClick = { onChapterSelected(index) },
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun ChapterItem(
    chapter: Chapter,
    isSelected: Boolean,
    onClick: () -> Unit,
    textColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(
                if (isSelected) colorResource(R.color.colorPrimary).copy(alpha = 0.1f)
                else Color.Transparent
            )
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chapter ${chapter.id}",
                fontSize = 12.sp,
                fontFamily = ibmplexsans,
                color = textColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chapter.title,
                fontSize = 16.sp,
                fontFamily = ibmplexsans,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = textColor
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = colorResource(R.color.colorPrimary),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
