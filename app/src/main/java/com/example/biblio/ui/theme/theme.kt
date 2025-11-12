package com.example.biblio.ui.theme

import com.example.biblio.R

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource

@Composable
fun BiblioTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = colorResource(id = R.color.colorPrimary),
        secondary = colorResource(id = R.color.colorSecondary),
        background = colorResource(id = R.color.colorBackground),
        surface = colorResource(id = R.color.colorBackground),
        onPrimary = colorResource(id = R.color.colorOnPrimary),
        onBackground = colorResource(id = R.color.colorOnBackground),
        onSurface = colorResource(id = R.color.colorOnBackground)
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}


