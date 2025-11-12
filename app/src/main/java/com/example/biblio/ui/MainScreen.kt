package com.example.biblio.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import com.example.biblio.ui.components.BottomBar
import com.example.biblio.ui.screens.BerandaScreen
import com.example.biblio.ui.screens.CariScreen
import com.example.biblio.ui.screens.KoleksiScreen

@Composable
fun MainScreen(fontFamily: FontFamily) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                fontFamily = fontFamily
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> BerandaScreen()
                1 -> CariScreen()
                2 -> KoleksiScreen()
            }
        }
    }
}
