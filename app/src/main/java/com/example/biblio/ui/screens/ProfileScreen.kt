package com.example.biblio.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.biblio.R
//import com.example.biblio.data.repository.UserRepository
import com.example.biblio.fraunces
import com.example.biblio.ui.components.Profile
import com.example.biblio.viewmodel.AuthViewModel
import com.example.biblio.viewmodel.UpdateState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel(),
    navController: NavController
) {

    var openNameDialog by remember { mutableStateOf(false) }
    var nameInput by remember { mutableStateOf("") }
    val openLogoutDialog = remember { mutableStateOf(false) }
    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        when (updateState) {
            is UpdateState.Success -> {
                openNameDialog = false
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = "Back",
                            tint = colorResource(R.color.colorOnSurfaceVariant)
                        )
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

                val user = Firebase.auth.currentUser
                user?.let {
                    Profile(
                        name = it.displayName ?: "Unknown",
                        fontSize = 48.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        navController = navController,
                        size = 96.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = it.displayName ?: "Unknown",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fraunces,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            MenuItem(
                painter = painterResource(R.drawable.text_fields_24px),
                title = "Style Text",
                subtitle = "Ubah gaya font aplikasi",
                onClick = {  }
            )

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

            // MENU LIST
            MenuItem(
                painter = painterResource(R.drawable.edit_24px),
                title = "Ubah Nama",
                subtitle = "Ubah nama tampilan",
                onClick = { openNameDialog = true }
            )

            MenuItem(
                painter = painterResource(R.drawable.manage_accounts_24px),
                title = "Manage Profile",
                subtitle = "Ubah password dan keamanan",
                onClick = {  }
            )

            MenuItem(
                painter = painterResource(R.drawable.logout_24px),
                title = "Keluar",
                subtitle = "Keluar sebagai pengguna",
                iconColor = MaterialTheme.colorScheme.error,
                onClick = { openLogoutDialog.value = true }
            )

            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outline)

            MenuItem(
                painter = painterResource(R.drawable.info_24px),
                title = "About Biblio",
                subtitle = "Informasi aplikasi",
                onClick = {  }
            )
        }
    }

    if (openNameDialog) {
        dialogInput(
            title = "Update Name",
            value = nameInput,
            onValueChange = { nameInput = it },
            onConfirm = {
                viewModel.updateName(
                    newName = nameInput,
                    onSuccess = {
                        openNameDialog = false  // Ganti jadi openNameDialog
                    },
                    onError = { error ->
                        // Tampilkan error
                    }
                )
            },
            onDismiss = { openNameDialog = false },  // Ganti jadi openNameDialog
            isLoading = updateState is UpdateState.Loading  // Tambahkan ini
        )
    }

    if (openLogoutDialog.value) {
        dialogAlert(
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
@Composable
fun MenuItem(
    painter: Painter,
    title: String,
    subtitle: String,
    iconColor: Color = colorResource(R.color.colorOnSurfaceVariant),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = title,
            modifier = Modifier.size(24.dp),
            tint = iconColor
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
fun dialogInput(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false
) {
    AlertDialog(

        containerColor = colorResource(com.example.biblio.R.color.colorBackground),      // warna background dialog
        titleContentColor = colorResource(com.example.biblio.R.color.colorOnBackground),         // warna judul
        textContentColor = colorResource(R.color.colorOnBackground),

        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    enabled = !isLoading
                )
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                enabled = !isLoading
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Batal")
            }
        }
    )
}

@Composable
fun dialogAlert(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(

        containerColor = colorResource(com.example.biblio.R.color.colorBackground),      // warna background dialog
        titleContentColor = colorResource(com.example.biblio.R.color.colorOnBackground),         // warna judul
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