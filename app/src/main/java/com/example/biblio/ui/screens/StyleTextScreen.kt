package com.example.biblio.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.theme.FontManager
import com.example.biblio.ui.viewmodel.StyleTextViewModel
import com.example.biblio.ui.viewmodel.StyleTextViewModelFactory
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.example.biblio.ui.theme.arvo
import com.example.biblio.ui.theme.interFont
import com.example.biblio.ui.theme.robotoFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StyleTextScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: StyleTextViewModel = viewModel(
        factory = StyleTextViewModelFactory(LocalContext.current)
    )
) {
    val fontStyle by viewModel.fontStyle.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Style Text",
                        fontFamily = FontManager.getFontFamily(fontStyle),
                        fontSize = FontManager.getHeadlineSize(fontSize)
                    )
                },
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
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Pilih Gaya Font",
                style = MaterialTheme.typography.headlineSmall,
                fontFamily = FontManager.getFontFamily(fontStyle),
                fontSize = FontManager.getHeadlineSize(fontSize)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Ubah tampilan font untuk pengalaman membaca yang lebih nyaman",
                fontSize = FontManager.getFontSize(fontSize),
                color = Color.Gray,
                fontFamily = FontManager.getFontFamily(fontStyle)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Section: Font Style
            Text(
                text = "Gaya Font",
                fontFamily = FontManager.getFontFamily(fontStyle),
                fontSize = FontManager.getFontSize(fontSize),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ganti bagian FontStyleOption untuk menggunakan font custom

// Default Font
            FontStyleOption(
                title = "Default",
                subtitle = "Font bawaan aplikasi",
                fontFamily = ibmplexsans,
                isSelected = fontStyle == "default",
                fontSize = FontManager.getFontSize(fontSize),
                onClick = { viewModel.updateFontStyle("default") }
            )

            Spacer(modifier = Modifier.height(12.dp))

// Roboto Font (Custom Download)
            FontStyleOption(
                title = "Roboto",
                subtitle = "Font modern dan bersih",
                fontFamily = robotoFont,                    // Font custom Anda
                isSelected = fontStyle == "roboto",
                fontSize = FontManager.getFontSize(fontSize),
                onClick = { viewModel.updateFontStyle("roboto") }
            )

            Spacer(modifier = Modifier.height(12.dp))

// Poppins Font (Custom Download)
            FontStyleOption(
                title = "arvo",
                subtitle = "Font geometris dan friendly",
                fontFamily = arvo,                   // Font custom Anda
                isSelected = fontStyle == "poppins",
                fontSize = FontManager.getFontSize(fontSize),
                onClick = { viewModel.updateFontStyle("poppins") }
            )

            Spacer(modifier = Modifier.height(12.dp))

// Inter Font (Custom Download)
            FontStyleOption(
                title = "Inter",
                subtitle = "Font optimal untuk layar",
                fontFamily = interFont,                     // Font custom Anda
                isSelected = fontStyle == "inter",
                fontSize = FontManager.getFontSize(fontSize),
                onClick = { viewModel.updateFontStyle("inter") }
            )

            // Section: Font Size
            Text(
                text = "Ukuran Font",
                fontFamily = FontManager.getFontFamily(fontStyle),
                fontSize = FontManager.getFontSize(fontSize),
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            val fontSizes = listOf("small", "medium", "large", "xlarge")
            val fontSizeLabels = mapOf(
                "small" to "Kecil",
                "medium" to "Sedang",
                "large" to "Besar",
                "xlarge" to "Sangat Besar"
            )

            fontSizes.forEach { size ->
                FontSizeOption(
                    label = fontSizeLabels[size] ?: size,
                    isSelected = fontSize == size,
                    fontFamily = FontManager.getFontFamily(fontStyle),
                    textSize = FontManager.getFontSize(size),
                    onClick = { viewModel.updateFontSize(size) }
                )

                if (size != fontSizes.last()) {
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Preview Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Preview",
                        fontFamily = FontManager.getFontFamily(fontStyle),
                        fontSize = FontManager.getHeadlineSize(fontSize),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Ini adalah preview teks dengan gaya font yang Anda pilih. Anda dapat melihat bagaimana teks akan terlihat di seluruh aplikasi.",
                        fontFamily = FontManager.getFontFamily(fontStyle),
                        fontSize = FontManager.getFontSize(fontSize),
                        lineHeight = FontManager.getFontSize(fontSize) * 1.5f
                    )
                }
            }
        }
    }
}

@Composable
fun FontStyleOption(
    title: String,
    subtitle: String,
    fontFamily: FontFamily,
    isSelected: Boolean,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) { // 1. Buat variabel terpisah untuk title
    val titleAnnotated = buildAnnotatedString {
        append(title)
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
            append("")  // Bold style diterapkan ke title
        }
    }

    // 2. Buat variabel terpisah untuk subtitle
    val subtitleAnnotated = buildAnnotatedString {
        append(subtitle)
        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
            append("")  // Italic style diterapkan ke subtitle
        }
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF1976D2), RoundedCornerShape(12.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = buildAnnotatedString {
                        // Gabungkan semua string di dalam blok ini
                        append("fontstyle ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(" ") // Ganti dengan variabel Anda, misal: book.title
                        }
                        append("\n") // Tambahkan baris baru di sini
                        append("example: ")
                        withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(" ") // Ganti dengan variabel Anda, misal: book.author
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "AaBbCc 123",
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    color = Color(0xFF1976D2)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
fun FontSizeOption(
    label: String,
    isSelected: Boolean,
    fontFamily: FontFamily,
    textSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (isSelected) {
                    Modifier.border(2.dp, Color(0xFF1976D2), RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0x00E3F2FD) else Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                fontFamily = fontFamily,
                fontSize = textSize
            )

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FontStyleSelector(
    selectedFontStyle: String,
    onFontStyleSelected: (String) -> Unit,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontFamily: FontFamily
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Contoh font style pilihan
        val fontStyles = listOf(
            "Default" to "default",
            "Roboto" to "roboto",
            "Arvo" to "arvo",
            "Inter" to "inter"
        )

        fontStyles.forEach { (displayName, fontStyleValue) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onFontStyleSelected(fontStyleValue) }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = buildAnnotatedString {
                        append("Font Style: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal)) {
                            append(displayName)
                        }
                    },
                    fontSize = fontSize,
                    fontFamily = fontFamily,
                    color = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.weight(1f))
                if (selectedFontStyle == fontStyleValue) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}