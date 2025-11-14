package com.example.biblio.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.viewmodel.AuthViewModel
import java.time.format.TextStyle

@Composable
fun SettingsScreen(navController: NavController) {

    val viewModel: AuthViewModel = viewModel()
    val openLogoutDialog = remember { mutableStateOf(false) }

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
                    onClick = {
                        openLogoutDialog.value = true
                    }
                ) {
                    Text("Logout")
                }

                if (openLogoutDialog.value) {
                    AlertDialogLogout(
                        onDismissRequest = { openLogoutDialog.value = false },
                        onConfirmation = {
                            openLogoutDialog.value = false
                            viewModel.logout()

                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        dialogTitle = "Keluar",
                        dialogText = "Yakin ingin Keluar?",
                        icon = Icons.Default.Info
                    )
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

@Composable
fun AlertDialogLogout(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(

        containerColor = colorResource(R.color.colorBackground),      // warna background dialog
        titleContentColor = colorResource(R.color.colorOnBackground),         // warna judul
        textContentColor = colorResource(R.color.colorOnBackground),

        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Batal")
            }
        }
    )
}