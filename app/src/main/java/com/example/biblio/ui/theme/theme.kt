package com.example.biblio.ui.theme

import com.example.biblio.R

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

@Composable
fun BiblioTheme(content: @Composable () -> Unit) {
    val colorScheme = lightColorScheme(
        primary = colorResource(id = R.color.colorPrimaryVariant),
        secondary = colorResource(id = R.color.colorSecondary),
        background = colorResource(id = R.color.colorBackground),
        surface = colorResource(id = R.color.colorBackground),
        onPrimary = colorResource(id = R.color.colorOnPrimary),
        onBackground = colorResource(id = R.color.colorOnBackground),
        onSurface = colorResource(id = R.color.colorOnBackground),

        primaryContainer = colorResource(id = R.color.colorPrimaryContainer),
        secondaryContainer = colorResource(id = R.color.colorSecondaryContainer),

        surfaceContainer = colorResource(id = R.color.colorSurfaceContainer),
        onPrimaryContainer = colorResource(id = R.color.colorOnPrimaryContainer),
        onSecondaryContainer = colorResource(id = R.color.colorOnSecondaryContainer),

        error = colorResource(id = R.color.colorError),
        onError = colorResource(id = R.color.colorOnError),
        errorContainer = colorResource(id = R.color.colorErrorContainer),
        onErrorContainer = colorResource(id = R.color.colorOnErrorContainer),

        outline = colorResource(id = R.color.colorOutline),
        outlineVariant = colorResource(id = R.color.colorOutlineVariant),

        surfaceVariant = colorResource(id = R.color.colorSurfaceVariant),
        onSurfaceVariant = colorResource(id = R.color.colorOnSurfaceVariant)
    )

    val typography = Typography(
        displayLarge = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.Bold,
            fontSize = 57.sp
        ),
        displayMedium = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.Bold,
            fontSize = 45.sp
        ),
        displaySmall = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.Bold,
            fontSize = 36.sp
        ),
        headlineLarge = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.SemiBold,
            fontSize = 32.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        ),
        titleLarge = TextStyle(
            fontFamily = fraunces,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp
        ),
        titleMedium = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        ),
        titleSmall = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        ),
        labelLarge = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        labelMedium = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        ),
        labelSmall = TextStyle(
            fontFamily = ibmplexsans,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
        )
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}


