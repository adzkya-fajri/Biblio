package com.example.biblio.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans

object FontManager {
    fun getFontFamily(style: String): FontFamily = when (style) {
        "default" -> ibmplexsans
        "serif" -> fraunces
        "roboto" -> robotoFont
        "arvo" -> arvo
        "inter" -> interFont
        else -> ibmplexsans
    }

    fun getFontSize(size: String): TextUnit = when (size) {
        "small" -> 12.sp
        "medium" -> 14.sp
        "large" -> 16.sp
        "xlarge" -> 18.sp
        else -> 14.sp
    }

    fun getHeadlineSize(size: String): TextUnit = when (size) {
        "small" -> 18.sp
        "medium" -> 22.sp
        "large" -> 26.sp
        "xlarge" -> 32.sp
        else -> 22.sp
    }
}
