package com.example.biblio.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biblio.R
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(
    fontSize: Int,
    lineSpacing: Float,
    onFontSizeChange: (Int) -> Unit,
    onLineSpacingChange: (Float) -> Unit,
    onDismiss: () -> Unit,
    isDarkMode: Boolean
) {
    // Tambahin haptic biar keren
    val haptic = LocalHapticFeedback.current

    val backgroundColor = if (isDarkMode) Color(0xFF2A2A2A) else colorResource(R.color.colorBackground)
    val textColor = if (isDarkMode) Color(0xFFE0E0E0) else colorResource(R.color.colorOnBackground)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = backgroundColor,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                text = "Pengaturan Tampilan",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = fraunces,
                color = textColor,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDarkMode) Color(0xFF1A1A1A) else colorResource(R.color.colorBackgroundVariant)
                )
            ) {
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                    fontSize = fontSize.sp,
                    fontFamily = ibmplexsans,
                    color = textColor,
                    lineHeight = fontSize.sp * lineSpacing,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Font Size Setting
            Text(
                text = "Ukuran Font",
                fontSize = 16.sp,
                fontFamily = ibmplexsans,
                fontWeight = FontWeight.Medium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "A",
                    fontSize = 14.sp,
                    fontFamily = ibmplexsans,
                    color = textColor.copy(alpha = 0.7f)
                )

                Slider(
                    value = fontSize.toFloat(),
                    onValueChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onFontSizeChange(it.toInt())
                    },
                    valueRange = 12f..24f,
                    steps = 5,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )

                Text(
                    text = "A",
                    fontSize = 24.sp,
                    fontFamily = ibmplexsans,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "${fontSize}sp",
                fontSize = 14.sp,
                fontFamily = ibmplexsans,
                color = textColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Line Spacing Setting
            Text(
                text = "Jarak Baris",
                fontSize = 16.sp,
                fontFamily = ibmplexsans,
                fontWeight = FontWeight.Medium,
                color = textColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "≡",
                    fontSize = 16.sp,
                    fontFamily = ibmplexsans,
                    color = textColor.copy(alpha = 0.7f)
                )

                Slider(
                    value = lineSpacing,
                    onValueChange = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onLineSpacingChange(it)
                    },
                    valueRange = 1.0f..2.5f,
                    steps = 5,
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp)
                )

                Text(
                    text = "≡",
                    fontSize = 24.sp,
                    fontFamily = ibmplexsans,
                    color = textColor.copy(alpha = 0.7f)
                )
            }

            Text(
                text = "${String.format("%.1f", lineSpacing)}x",
                fontSize = 14.sp,
                fontFamily = ibmplexsans,
                color = textColor.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}