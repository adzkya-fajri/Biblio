package com.example.biblio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.biblio.ui.navigation.AppNavHost
import com.example.biblio.ui.theme.BiblioTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// LAYOUT-LAYOUT

// CUSTOM FONT dari file .ttf di res/font/
val ibmplexsans = FontFamily(
    Font(R.font.ibmplexsans_regular, FontWeight.Normal),
    Font(R.font.ibmplexsans_medium, FontWeight.Medium)
)

val ibmplexmono = FontFamily(
    Font(R.font.ibmplexmono_regular, FontWeight.Normal),
)

val fraunces = FontFamily(
    Font(R.font.fraunces_bold, FontWeight.Bold),
    Font(R.font.fraunces_semibold, FontWeight.SemiBold)
)

private lateinit var auth: FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
//            navigationBarStyle = SystemBarStyle.auto(
//                lightScrim = getColor(R.color.colorBackground), // warna navbar
//                darkScrim = getColor(R.color.colorBackground)
//            )
        )

        // Inisialisasi Firebase auth
        auth = Firebase.auth

        enableEdgeToEdge()
        actionBar?.hide()
        setContent {
            BiblioTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .safeDrawingPadding()
                ) {
                    AppNavHost()
                }
            }
        }
    }
}