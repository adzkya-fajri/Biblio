package com.example.biblio.ui.components

//import com.example.biblio.Section
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.model.Section
import com.example.biblio.fraunces
import com.example.biblio.viewmodel.BukuViewModel

@Composable
fun SectionItem(section: Section,
                navController: NavController? = null,  // ← Tambah parameter
                viewModel: BukuViewModel  // ← TAMBAHAN
                ) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title
        Text(
            text = section.title,  // ← Ganti 'name' jadi 'title'
            fontSize = 20.sp,
            fontFamily = fraunces,
            color = colorResource(R.color.colorOnBackground),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Horizontal scroll untuk books (LazyRow)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(
                items = section.books,
                key = { book -> book.id } // ← Ganti 'isbn' jadi 'id' untuk key
            ) { book ->
                BookItem(
                    book = book,
                    navController = navController,  // ← Pass navController
                    viewModel = viewModel // ✅ PASS BOTH!
                )
            }
        }
    }
}