package com.example.biblio.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.biblio.R

// Contoh: Jika font file Anda bernama "roboto.ttf", "roboto_bold.ttf", dll
//fraunces_semibold
val robotoFont = FontFamily(
    Font(R.font.fraunces_bold, FontWeight.Normal),
    Font(R.font.fraunces_semibold, FontWeight.Bold)
)
val arvo = FontFamily(
    Font(R.font.arvo_regular, FontWeight.Normal),
    Font(R.font.arvo_bold, FontWeight.Bold)
)
// Contoh lain: Jika font file Anda bernama "poppins.ttf", "poppins_semibold.ttf", dll
//val poppinsFont = FontFamily(
//    Font(R.font.poppins, FontWeight.Normal),
//    Font(R.font.poppins_semibold, FontWeight.SemiBold),
//    Font(R.font.poppins_bold, FontWeight.Bold)
//)
//mono
val interFont = FontFamily(
    Font(R.font.ibmplexmono_regular, FontWeight.Normal)
)

// Contoh lain: Jika font file Anda bernama "inter.ttf", "inter_medium.ttf", dll
//val interFont = FontFamily(
//    Font(R.font.inter, FontWeight.Normal),
//    Font(R.font.inter_medium, FontWeight.Medium),
//    Font(R.font.inter_bold, FontWeight.Bold)
//)

// Tambahkan lebih banyak font sesuai kebutuhan Anda