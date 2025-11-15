package com.example.biblio.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.biblio.data.repository.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNameScreen(
    onNavigateBack: () -> Unit = {},
//    userViewModel: UserViewModel = viewModel(
//        factory = UserViewModelFactory(UserRepository(LocalContext.current))
//    )
) {
//    val userProfile by userViewModel.userProfile.collectAsState()
//    var name by remember { mutableStateOf(userProfile.name) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Name") },
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
                text = "Ubah Nama Tampilan",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(16.dp))

//            OutlinedTextField(
//                value = name,
//                onValueChange = { name = it },
//                label = { Text("Nama") },
//                modifier = Modifier.fillMaxWidth()
//            )

            Spacer(modifier = Modifier.height(16.dp))

//            Button(
//                onClick = {
//                    userViewModel.updateName(name)
//                    onNavigateBack()
//                },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Simpan")
//            }
        }
    }
}