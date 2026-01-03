//package com.example.biblio.ui.components
//
//import androidx.compose.animation.AnimatedContentScope
//import androidx.compose.animation.ExperimentalSharedTransitionApi
//import androidx.compose.animation.SharedTransitionScope
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.colorResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavController
//import com.example.biblio.R
//import com.example.biblio.data.model.SectionWithBooks
//import com.example.biblio.fraunces
//import com.example.biblio.viewmodel.BookViewModel
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun BigSectionItem(
//    sectionWithBooks: SectionWithBooks,
//    navController: NavController? = null,
//    viewModel: BookViewModel,
//    sharedTransitionScope: SharedTransitionScope,
//    animatedContentScope: AnimatedContentScope
//) {
//    val section = sectionWithBooks.section
//    val books = sectionWithBooks.books
//    val favoriteIds by viewModel.favoriteIds.collectAsState()
//
//    Column(modifier = Modifier.fillMaxWidth()) {
//        // Section Title
//        Text(
//            text = section.title,
//            fontSize = 28.sp,
//            fontWeight = FontWeight.Bold,
//            fontFamily = fraunces,
//            color = colorResource(R.color.colorOnBackground),
//            modifier = Modifier.padding(horizontal = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        LazyRow(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.spacedBy(12.dp),
//            contentPadding = PaddingValues(horizontal = 16.dp)
//        ) {
//            items(
//                items = books,
//                key = { it.id }
//            ) { book ->
//                BookItem(
//                    book = book,
//                    navController = navController,
//                    sharedTransitionScope = sharedTransitionScope,
//                    animatedContentScope = animatedContentScope,
//                    isFavorite = book.id in favoriteIds,
//                    onToggleFavorite = viewModel::toggleFavorite
//                )
//            }
//        }
//    }
//}