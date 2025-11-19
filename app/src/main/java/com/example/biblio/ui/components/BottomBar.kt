package com.example.biblio.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import com.example.biblio.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontFamily

@Composable
fun BottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    fontFamily: FontFamily
) {
    NavigationBar(
        containerColor = colorResource(id = R.color.colorBackground),
        contentColor = colorResource(id = R.color.colorBackground)
    ) {
        val tabs = listOf(
            Triple("Beranda", R.drawable.ic_biblio, 0),
            Triple("Cari", R.drawable.search_24px, 1),
            Triple("Koleksi", R.drawable.newsstand_24px, 2)
        )

        tabs.forEach { (label, iconRes, index) ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick = { onTabSelected(index) },
                icon = { Icon(painter = painterResource(id = iconRes), contentDescription = label) },
                label = {
                    Text(
                        label,
                        fontFamily = fontFamily,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
            )
        }
    }
}