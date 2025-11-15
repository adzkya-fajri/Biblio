package com.example.biblio.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.biblio.data.repository.UserRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleTextScreen(
    onNavigateBack: () -> Unit = {},
//    userViewModel: UserViewModel = viewModel(
//        factory = UserViewModelFactory(UserRepository(LocalContext.current))
//    )
) {
//    val userProfile by userViewModel.userProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Style Text") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Pilih Gaya Font",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ubah tampilan font untuk pengalaman membaca yang lebih nyaman",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Default Font
//            FontStyleOption(
//                title = "Default",
//                subtitle = "Font bawaan aplikasi",
//                fontFamily = ibmplexsans,
//                isSelected = userProfile.fontStyle == "default",
//                onClick = { userViewModel.updateFontStyle("default") }
//            )

            Spacer(modifier = Modifier.height(12.dp))

            // Serif Font
//            FontStyleOption(
//                title = "Serif",
//                subtitle = "Font klasik untuk membaca",
//                fontFamily = fraunces,
//                isSelected = userProfile.fontStyle == "serif",
//                onClick = { userViewModel.updateFontStyle("serif") }
//            )

            Spacer(modifier = Modifier.height(12.dp))

            // Sans Serif Font
//            FontStyleOption(
//                title = "Sans Serif",
//                subtitle = "Font modern dan bersih",
//                fontFamily = FontFamily.SansSerif,
//                isSelected = userProfile.fontStyle == "sans",
//                onClick = { userViewModel.updateFontStyle("sans") }
//            )

            Spacer(modifier = Modifier.height(12.dp))

            // Monospace Font
//            FontStyleOption(
//                title = "Monospace",
//                subtitle = "Font dengan lebar sama",
//                fontFamily = FontFamily.Monospace,
//                isSelected = userProfile.fontStyle == "monospace",
//                onClick = { userViewModel.updateFontStyle("monospace") }
//            )
        }
    }
}

@Composable
fun FontStyleOption(
    title: String,
    subtitle: String,
    fontFamily: FontFamily,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF1976D2), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    fontFamily = fontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AaBbCc 123",
                    fontSize = 16.sp,
                    fontFamily = fontFamily,
                    color = Color(0xFF1976D2)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
