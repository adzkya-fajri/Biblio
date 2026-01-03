package com.example.biblio.ui.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.model.Book
import com.example.biblio.data.model.Section
import com.example.biblio.data.model.SectionWithBooks
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.viewmodel.BookViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SectionItem(
    section: Section,
    books: List<Book>,
    isLoading: Boolean,
    navController: NavController? = null,
    viewModel: BookViewModel,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope
) {
    val favoriteIds by viewModel.favoriteIds.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = section.title,
            fontSize = 20.sp,
            fontFamily = fraunces,
            color = colorResource(R.color.colorOnBackground),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = colorResource(R.color.colorPrimary)
                    )
                }
            }
            books.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Tidak ada buku",
                        color = colorResource(R.color.colorOnBackground).copy(alpha = 0.5f),
                        fontFamily = ibmplexsans
                    )
                }
            }
            else -> {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(books, key = { it.id }) { book ->
                        BookItem(
                            book = book,
                            sectionId = section.id,
                            onClick = {
                                // ✅ Pass sectionId di URL
                                navController?.navigate("detail/${book.id}?sectionId=${section.id}")
                            },
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
                            isFavorite = book.id in favoriteIds,
                            onToggleFavorite = viewModel::toggleFavorite
                        )
                    }
                }
            }
        }
    }
}