package com.example.biblio.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.R
import com.example.biblio.data.model.Buku
import com.example.biblio.ibmplexsans
import com.example.biblio.viewmodel.BukuViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun BookItem(
    book: Buku,
    navController: NavController? = null,
    viewModel: BukuViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    coverHeight: Dp = 180.dp,
    coverWidth: Dp = 120.dp,
) {
    val favoriteIds by viewModel.favoriteIds.collectAsState(initial = emptySet())
    val isFavorite = favoriteIds.contains(book.id.toString())

    Column(
        modifier = Modifier
            .width(coverWidth)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = LocalIndication.current
            ) {
                navController?.navigate("buku/${book.id}")
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(coverHeight)
        ) {
            with(sharedTransitionScope) {
                Card(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxSize()
                        .sharedElement(
                            state = rememberSharedContentState(key = "cover-${book.id}"),
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

            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(32.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 2.dp
            ) {
                IconButton(
                    onClick = { viewModel.toggleFavorite(book.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        with(sharedTransitionScope) {
            Text(
                color = colorResource(id = R.color.colorOnBackground),
                text = book.judul,
                lineHeight = 1.25.em,
                fontSize = 14.sp,
                fontFamily = ibmplexsans,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "title-${book.id}"),
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
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        with(sharedTransitionScope) {
            Text(
                text = book.penulis,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                fontFamily = ibmplexsans,
                maxLines = 1,
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "author-${book.id}"),
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
            )
        }
    }
}