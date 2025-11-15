package com.example.biblio.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblio.viewmodel.AuthState
import com.example.biblio.viewmodel.AuthViewModel
import java.time.format.TextStyle

@Composable
fun SettingsScreen(
    onLogoutSuccess: () -> Unit
) {

    val viewModel: AuthViewModel = viewModel()
    val authState by viewModel.authState.collectAsState()

    // Navigasi otomatis setelah logout
    LaunchedEffect(authState) {
        if (authState == AuthState.Idle) {
            onLogoutSuccess()
        }
    }

    val list = listOf(
        "C++", "C", "C#", "Java", "Kotlin", "Dart", "Python", "Javascript", "SpringBoot",
        "XML", "Dart", "Node JS", "Typescript", "Dot Net", "GoLang", "MongoDb",
    )
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LazyColumn",
            modifier = Modifier.padding(32.dp),
            style = androidx.compose.ui.text.TextStyle(
                color = Color.Magenta,
                fontSize = TextUnit(value = 24f, type = TextUnitType.Sp)
            ),
            fontWeight = FontWeight.ExtraBold
        )

        // lazy column for displaying listview.
        LazyColumn {
            // ✅ FIXED: Button di item terpisah
            item {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Logout")
                }
            }

            // ✅ FIXED: items() di luar item()
            items(list) { language ->
                Column(
                    modifier = Modifier.clickable { /* action */ }
                ) {
                    Text(language, modifier = Modifier.padding(15.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}