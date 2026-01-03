package com.example.biblio.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.data.model.Book
import com.example.biblio.fraunces
import com.example.biblio.ibmplexmono
import com.example.biblio.ibmplexsans
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookContent(
    book: Book,
    sectionId: String?,
    navController: NavController,
    bottomPadding: Dp,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    coverHeight: Dp,
    coverWidth: Dp,
    onToggleFavorite: (String) -> Unit
) {
    val coverKey = if (sectionId != null) {
        "cover-${sectionId}-${book.id}"
    } else {
        "cover-${book.id}" // fallback untuk direct access
    }

    val titleKey = if (sectionId != null) {
        "title-${sectionId}-${book.id}"
    } else {
        "title-${book.id}"
    }

    val authorKey = if (sectionId != null) {
        "author-${sectionId}-${book.id}"
    } else {
        "author-${book.id}"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        colorResource(R.color.colorBackground),
                        colorResource(R.color.colorBackgroundVariant)
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = bottomPadding, top = 10.dp)
        ) {

            item {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                with(sharedTransitionScope) {
                    Card(
                        modifier = Modifier
                            .background(Color.Transparent)
                            .height(coverHeight)
                            .width(coverWidth)
                            .sharedElement(
                                state = rememberSharedContentState(key = coverKey),
                                animatedVisibilityScope = animatedContentScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                }
                            ),
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
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                with(sharedTransitionScope) {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = titleKey),
                                animatedVisibilityScope = animatedContentScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                },
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                            .skipToLookaheadSize(),
                        color = colorResource(id = R.color.colorOnBackground),
                        text = book.title,
                        fontSize = 24.sp,
                        fontFamily = fraunces,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                with(sharedTransitionScope) {
                    Text(
                        text = book.author,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = authorKey),
                                animatedVisibilityScope = animatedContentScope,
                                boundsTransform = { _, _ ->
                                    spring(
                                        dampingRatio = Spring.DampingRatioLowBouncy,
                                        stiffness = Spring.StiffnessMediumLow
                                    )
                                },
                                resizeMode = SharedTransitionScope.ResizeMode.ScaleToBounds()
                            )
                            .skipToLookaheadSize(),
                        fontSize = 14.sp,
                        fontFamily = ibmplexsans,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }

            item {
                Text(
                    text = "ISBN ${book.isbn}",
                    fontFamily = ibmplexmono,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.star_24px),
                            contentDescription = null,
                            tint = colorResource(R.color.colorPrimaryVariant),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Ulasan", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                            Text("4.5", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.book_24px),
                            contentDescription = null,
                            tint = colorResource(R.color.colorPrimaryVariant),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Halaman", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                            Text("${book.pages}", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.timer_24px),
                            contentDescription = null,
                            tint = colorResource(R.color.colorPrimaryVariant),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("Durasi", fontSize = 12.sp, color = Color.Gray, fontFamily = ibmplexsans, lineHeight = 1.25.em)
                            Text("5j 30m*", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Text(
                    color = colorResource(id = R.color.colorOnBackground),
                    text = book.description,
                    fontSize = 14.sp,
                    lineHeight = 1.5.em,
                    fontFamily = ibmplexsans,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            book?.let {
                                val bookJson = Json.encodeToString(it)
                                val encoded = URLEncoder.encode(bookJson, "UTF-8")
                                navController.navigate("reader/$encoded")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.book_5_24px),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Beli & Baca", fontFamily = ibmplexsans)
                    }

                    OutlinedButton(
                        onClick = { onToggleFavorite(book.id) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.newsstand_24px),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tambahkan ke Koleksi", fontFamily = ibmplexsans)
                    }
                }
            }
        }
    }
}
