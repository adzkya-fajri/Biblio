package com.example.biblio.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.FavoriteRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexmono
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.SectionItem
import com.example.biblio.viewmodel.BukuViewModel
import com.example.biblio.viewmodel.BukuViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BukuScreen(
    bookId: String?,
    bottomPadding: Dp,
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    coverHeight: Dp = 225.dp,
    coverWidth: Dp = 150.dp,
    viewModel: BukuViewModel
) {
    val bookDatabase by viewModel.bookDatabase.collectAsState()
    var cachedBook by remember { mutableStateOf<com.example.biblio.data.model.Buku?>(null) }
    var isTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(bookId, bookDatabase) {
        if (bookDatabase == null) {
            delay(5000)
            isTimeout = true
        }
    }

    val book = remember(bookId, bookDatabase) {
        bookDatabase?.sections
            ?.flatMap { it.books }
            ?.find { it.id.toString() == bookId }
            ?.also { cachedBook = it }
            ?: cachedBook
    }

    if (book == null && isTimeout) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Buku tidak ditemukan")
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(id = R.color.colorBackground),
                            colorResource(id = R.color.colorBackgroundVariant)
                        )
                    )
                )
        ) {
            // Loading overlay
            if (bookDatabase == null && !isTimeout && cachedBook != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

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
                                    state = rememberSharedContentState(key = "cover-${book?.id}"),
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
                                model = book?.cover,
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
                                    sharedContentState = rememberSharedContentState(key = "title-${book?.id}"),
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
                            text = book?.judul ?: "",
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
                            text = book?.penulis ?: "",
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .sharedBounds(
                                    sharedContentState = rememberSharedContentState(key = "author-${book?.id}"),
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
                        text = "ISBN ${book?.isbn ?: "-"}",
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
                                Text("350", color = colorResource(id = R.color.colorOnBackground), fontSize = 14.sp, fontFamily = ibmplexsans, fontWeight = FontWeight.Bold)
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
                        text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Ut eleifend semper fringilla. Vestibulum convallis rutrum arcu, et dapibus arcu sollicitudin eu. Duis gravida faucibus maximus.",
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
                            onClick = { book?.let { viewModel.toggleFavorite(it.id) } },
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

                item { Spacer(modifier = Modifier.height(16.dp)) }

//                val sections = bookDatabase?.sections ?: emptyList()
//                if (sections.isNotEmpty()) {
//                    items(
//                        items = sections,
//                        key = { section -> section.id }
//                    ) { section ->
//                        SectionItem(
//                            section = section,
//                            navController = navController,
//                            viewModel = viewModel,
//                            sharedTransitionScope = sharedTransitionScope,
//                            animatedContentScope = animatedContentScope
//                        )
//                    }
//                }
            }
        }
    }
}