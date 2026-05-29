package com.example.biblio.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun ImmersiveMode() {
    val view = LocalView.current

    DisposableEffect(view) {
        // Find the hosting Activity's window
        val window = (view.context as? Activity)?.window ?: return@DisposableEffect onDispose {}
        val insetsController = WindowCompat.getInsetsController(window, view)

        // 1. Enter Immersive Mode
        insetsController.apply {
            // Hide both status bar and navigation bar
            hide(WindowInsetsCompat.Type.systemBars())

            // BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE makes system bars appear semi-transparently
            // on an edge swipe and automatically disappear after a few seconds (Sticky Immersive)
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        // 2. Exit Immersive Mode when leaving this screen
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }
}