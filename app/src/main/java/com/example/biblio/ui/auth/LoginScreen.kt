package com.example.biblio.ui.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.theme.BiblioTheme
import com.example.biblio.viewmodel.AuthState
import com.example.biblio.viewmodel.AuthViewModel
import com.example.biblio.viewmodel.GoogleAuthState
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = viewModel(),
    navController: NavController? = null,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val authState by viewModel.authState.collectAsState()
    val googleAuthState by viewModel.googleAuthState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState, googleAuthState) {
        if (authState is AuthState.Success || googleAuthState is GoogleAuthState.Success) {
            onLoginSuccess()
        }
    }

    LoginScreenContent(
        email = email,
        password = password,
        passwordVisible = passwordVisible,
        googleAuthState = googleAuthState,
        authState = authState,
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onLoginClick = { viewModel.login(email, password) },
        onGoogleSignIn = {
            viewModel.googleLogin(context)
        },
        onForgotPassword = { },
        onRegisterClick = { navController?.navigate("register") }
    )
}

@Composable
fun LoginScreenContent(
    email: String,
    password: String,
    passwordVisible: Boolean,
    googleAuthState: GoogleAuthState,
    authState: AuthState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onLoginClick: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.logo_biblio),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = colorResource(id = R.color.colorPrimaryVariant)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Masuk ke Biblio",
                fontFamily = fraunces,
                fontSize = 32.sp,
                lineHeight = 1.2.em,
                color = colorResource(id = R.color.colorOnBackground),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            colorResource(R.color.colorBackground)
                        ),
                        startY = 0f,
                        endY = 100f
                    )
                )
                .padding(top = 50.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email", fontFamily = ibmplexsans) },
                singleLine = true,
                enabled = authState !is AuthState.Loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password", fontFamily = ibmplexsans) },
                singleLine = true,
                enabled = authState !is AuthState.Loading,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (passwordVisible) R.drawable.visibility_off_24px else R.drawable.visibility_24px
                    IconButton(onClick = onPasswordVisibilityToggle) {
                        Icon(painter = painterResource(id = icon), contentDescription = null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (authState is AuthState.Error) {
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontFamily = ibmplexsans,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLoginClick,
                enabled = authState !is AuthState.Loading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text("Masuk", fontFamily = ibmplexsans)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onGoogleSignIn,
                enabled = googleAuthState !is GoogleAuthState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                border = BorderStroke(1.dp, colorResource(id = R.color.colorPrimaryVariant))

            ) {

                if (googleAuthState is GoogleAuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.logo_google),
                        contentDescription = "Google Logo",
                        tint = Color.Unspecified
                    )

                    Spacer(Modifier.width(8.dp))

                    Text("Masuk menggunakan Google", fontFamily = ibmplexsans)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onForgotPassword) {
                    Text("Lupa password?", fontFamily = ibmplexsans)
                }
                TextButton(onClick = onRegisterClick) {
                    Text("Belum punya akun?", fontFamily = ibmplexsans)
                }
            }
        }
    }
}

// PREVIEW

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    BiblioTheme {
        LoginScreenContent(
            email = "user@example.com",
            password = "password123",
            passwordVisible = false,
            googleAuthState = GoogleAuthState.Idle,
            authState = AuthState.Idle,
            onEmailChange = { },
            onPasswordChange = { },
            onPasswordVisibilityToggle = { },
            onLoginClick = { },
            onGoogleSignIn = { },
            onForgotPassword = { },
            onRegisterClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingEmailPreview() {
    BiblioTheme {
        LoginScreenContent(
            email = "user@example.com",
            password = "password123",
            passwordVisible = false,
            googleAuthState = GoogleAuthState.Idle,
            authState = AuthState.Loading,
            onEmailChange = { },
            onPasswordChange = { },
            onPasswordVisibilityToggle = { },
            onLoginClick = { },
            onGoogleSignIn = { },
            onForgotPassword = { },
            onRegisterClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenLoadingGooglePreview() {
    BiblioTheme {
        LoginScreenContent(
            email = "user@example.com",
            password = "password123",
            passwordVisible = false,
            googleAuthState = GoogleAuthState.Loading,
            authState = AuthState.Idle,
            onEmailChange = { },
            onPasswordChange = { },
            onPasswordVisibilityToggle = { },
            onLoginClick = { },
            onGoogleSignIn = { },
            onForgotPassword = { },
            onRegisterClick = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenErrorPreview() {
    BiblioTheme {
        LoginScreenContent(
            email = "user@example.com",
            password = "wrongpassword",
            passwordVisible = false,
            googleAuthState = GoogleAuthState.Error("Terjadi kesalahan"),
            authState = AuthState.Error("Email atau password salah"),
            onEmailChange = { },
            onPasswordChange = { },
            onPasswordVisibilityToggle = { },
            onLoginClick = { },
            onGoogleSignIn = { },
            onForgotPassword = { },
            onRegisterClick = { }
        )
    }
}