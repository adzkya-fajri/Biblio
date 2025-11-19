package com.example.biblio.ui.components

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.biblio.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil.compose.AsyncImage
import com.example.biblio.ibmplexsans
import kotlinx.coroutines.delay

@Composable
fun NowReadingBar(
    bookTitle: String,
    bookAuthor: String,
    bookCover: String,
    colorContainer: Color = colorResource(R.color.colorBackgroundVariant),
    currentPage: Int,
    totalPages: Int,
    onContinueReading: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (colorContainer.luminance() > 0.5f) Color.Black else Color.White

    var isClosing by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isClosing) 0.8f else 1f,
        animationSpec = tween(200)
    )
    val offsetY by animateDpAsState(
        targetValue = if (isClosing) 100.dp else 0.dp,
        animationSpec = tween(300)
    )
    val alpha by animateFloatAsState(
        targetValue = if (isClosing) 0f else 1f,
        animationSpec = tween(200)
    )

    LaunchedEffect(isClosing) {
        if (isClosing) {
            delay(500) // tunggu animasi selesai
            onDismiss()
        }
    }

    Column(
        modifier = modifier
            .scale(scale) // ✅ tambah scale
            .alpha(alpha) // ✅ tambah alpha
            .offset(y = offsetY)
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(2.dp),
            shape = RoundedCornerShape(8.dp, 8.dp, 8.dp, 8.dp), // rounded top
            colors = CardDefaults.cardColors(
                containerColor = colorContainer
            )
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = bookCover,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.book_2),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(36.dp)
                            .height(48.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "$bookTitle • $bookAuthor",
                            fontSize = 14.sp,
                            fontFamily = ibmplexsans,
                            fontWeight = FontWeight.Medium,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Halaman $currentPage dari $totalPages",
                            fontFamily = ibmplexsans,
                            color = contentColor.copy(alpha = 0.7f),
                            fontSize = 12.sp,
                        )
                    }

                    IconButton(
                        onClick = { isClosing = true }, // ✅ trigger animasi
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                LinearProgressIndicator(
                    progress = { currentPage.toFloat() / totalPages.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = contentColor,
                    trackColor = contentColor.copy(alpha = 0.2f),
                )
            }
        }
    }
}