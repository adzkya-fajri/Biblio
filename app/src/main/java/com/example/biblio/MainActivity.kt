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

import com.example.biblio.ui.navigation.NavHost
import com.example.biblio.ui.theme.BiblioTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

// LAYOUT-LAYOUT

// CUSTOM FONT dari file .ttf di res/font/
val ibmplexsans = FontFamily(
    Font(R.font.ibmplexsans_regular, FontWeight.Normal),
    Font(R.font.ibmplexsans_medium, FontWeight.Bold),
)

val fraunces = FontFamily(
    Font(R.font.fraunces, FontWeight.Normal),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge(
////            statusBarStyle = SystemBarStyle.auto(
////                lightScrim = Color.White.toArgb(),   // warna terang
////                darkScrim = Color.Black.toArgb()     // warna gelap
////            ),
//            navigationBarStyle = SystemBarStyle.auto(
//                lightScrim = Color.Transparent,
//                darkScrim = Color.Transparent
//            )
//        )

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
                    NavHost()
                }
            }
        }
    }
}