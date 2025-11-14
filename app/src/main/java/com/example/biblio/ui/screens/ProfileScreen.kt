package com.example.biblio.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.biblio.data.repository.UserRepository
import com.example.biblio.fraunces
import com.example.biblio.viewmodel.UserViewModel
import com.example.biblio.viewmodel.UserViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToEditName: () -> Unit = {},
    onNavigateToManageProfile: () -> Unit = {},
    onNavigateToStyleText: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(UserRepository(LocalContext.current))
    )
) {
    val userProfile by userViewModel.userProfile.collectAsState()
    var showPhotoDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
                .verticalScroll(rememberScrollState())
        ) {
            // HEADER: Foto Profil + Nama
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto Profil Besar
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1976D2))
                        .border(4.dp, Color.White, CircleShape)
                        .clickable { showPhotoDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    if (userProfile.photoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = userProfile.photoUrl,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = userProfile.name.first().uppercase(),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Icon Camera
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(2.dp, Color(0xFF1976D2), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Photo",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userProfile.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = fraunces
                )

                Text(
                    text = userProfile.email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Divider()

            // MENU LIST
            ProfileMenuItem(
                icon = Icons.Default.Person,
                title = "Custom Profile",
                subtitle = "Lihat dan edit profil",
                onClick = { /* Already in profile screen */ }
            )

            ProfileMenuItem(
                icon = Icons.Default.Edit,
                title = "Custom Name",
                subtitle = "Ubah nama tampilan",
                onClick = onNavigateToEditName
            )

            ProfileMenuItem(
                icon = Icons.Default.TextFields,
                title = "Style Text",
                subtitle = "Ubah gaya font aplikasi",
                onClick = onNavigateToStyleText
            )

            ProfileMenuItem(
                icon = Icons.Default.Lock,
                title = "Manage Profile",
                subtitle = "Ubah password dan keamanan",
                onClick = onNavigateToManageProfile
            )

            ProfileMenuItem(
                icon = Icons.Default.Info,
                title = "About Biblio",
                subtitle = "Informasi aplikasi",
                onClick = onNavigateToAbout
            )
        }
    }

    // Dialog untuk ganti foto
    if (showPhotoDialog) {
        PhotoPickerDialog(
            onDismiss = { showPhotoDialog = false },
            onPhotoSelected = { url ->
                userViewModel.updatePhoto(url)
                showPhotoDialog = false
            }
        )
    }
}
@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = Color(0xFF1976D2)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Navigate",
            tint = Color.Gray
        )
    }
}

@Composable
fun PhotoPickerDialog(
    onDismiss: () -> Unit,
    onPhotoSelected: (String) -> Unit
) {
    var photoUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ubah Foto Profil") },
        text = {
            Column {
                Text("Masukkan URL foto profil:")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    placeholder = { Text("https://example.com/photo.jpg") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Atau gunakan foto default",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onPhotoSelected(photoUrl) },
                enabled = photoUrl.isNotEmpty()
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
