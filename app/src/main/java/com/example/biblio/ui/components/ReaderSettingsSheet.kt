package com.example.biblio.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.biblio.ibmplexsans
import com.example.biblio.viewmodel.ReaderTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    currentTheme: ReaderTheme,
    currentFontSize: Double,
    onThemeChange: (ReaderTheme) -> Unit,
    onFontSizeChange: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val resolvedTheme = when (currentTheme) {
        ReaderTheme.AUTO -> if (isSystemDark) ReaderTheme.DARK else ReaderTheme.LIGHT
        else -> currentTheme
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = when (resolvedTheme) {
            ReaderTheme.DARK -> Color(0xFF1A1A1A)
            ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
            else -> Color.White
        },
        contentColor = if (resolvedTheme == ReaderTheme.DARK) Color.White else Color.Black
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                text = "Pengaturan Teks",
                style = MaterialTheme.typography.titleLarge,
                fontFamily = ibmplexsans,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // THEME PICKER
            Text(
                text = "Tema",
                style = MaterialTheme.typography.labelLarge,
                fontFamily = ibmplexsans,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ThemeOption(
                    color = Color.White,
                    isSelected = currentTheme == ReaderTheme.LIGHT,
                    onClick = { onThemeChange(ReaderTheme.LIGHT) }
                )
                ThemeOption(
                    color = Color(0xFFF4ECD8),
                    isSelected = currentTheme == ReaderTheme.SEPIA,
                    onClick = { onThemeChange(ReaderTheme.SEPIA) }
                )
                ThemeOption(
                    color = Color(0xFF1A1A1A),
                    isSelected = currentTheme == ReaderTheme.DARK,
                    onClick = { onThemeChange(ReaderTheme.DARK) }
                )
                ThemeOption(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    icon = Icons.Default.AutoMode,
                    isSelected = currentTheme == ReaderTheme.AUTO,
                    onClick = { onThemeChange(ReaderTheme.AUTO) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FONT SIZE PICKER
            Text(
                text = "Ukuran Huruf",
                style = MaterialTheme.typography.labelLarge,
                fontFamily = ibmplexsans,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onFontSizeChange((currentFontSize - 0.1).coerceAtLeast(0.5)) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease Font Size")
                }

                Text(
                    text = "${(currentFontSize * 100).toInt()}%",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    fontFamily = ibmplexsans
                )

                IconButton(
                    onClick = { onFontSizeChange((currentFontSize + 0.1).coerceAtMost(3.0)) },
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase Font Size")
                }
            }
        }
    }
}

@Composable
fun ThemeOption(
    color: Color,
    icon: ImageVector? = null,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp, 48.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
