package com.example.biblio.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.repository.BookContentRepository
import com.example.biblio.fraunces
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.ReaderSettingsSheet
import com.example.biblio.ui.components.TableOfContentsSheet
import com.example.biblio.viewmodel.BookReaderViewModel
import com.example.biblio.viewmodel.BookReaderViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookReaderScreen(
    bookId: String,
    preview: Boolean,
    navController: NavController,
    viewModel: BookReaderViewModel = viewModel(
        factory = BookReaderViewModelFactory(
            BookContentRepository(LocalContext.current)
        )
    )
) {
    LaunchedEffect(bookId, preview) {
        viewModel.loadBook(bookId, preview)
    }

    val bookInfo by viewModel.bookInfo.collectAsState()
    val bookContent by viewModel.bookContent.collectAsState()
    val fileUrl by viewModel.fileUrl.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val currentChapterIndex by viewModel.currentChapterIndex.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showTableOfContents by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { snackbarHostState.showSnackbar(it) }
    }

    val backgroundColor =
        if (settings.isDarkMode) Color(0xFF1A1A1A) else colorResource(R.color.colorBackground)
    val textColor =
        if (settings.isDarkMode) Color(0xFFE0E0E0) else colorResource(R.color.colorOnBackground)

    Scaffold(
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            BottomAppBar(
                containerColor = if (settings.isDarkMode) Color(0xFF2A2A2A)
                else colorResource(R.color.colorBackground),
            ) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painter = painterResource(R.drawable.arrow_back_24px),
                        contentDescription = "Kembali",
                        tint = textColor
                    )
                }
                IconButton(onClick = { viewModel.toggleDarkMode() }) {
                    Icon(
                        imageVector = if (settings.isDarkMode) Icons.Default.LightMode
                        else Icons.Default.DarkMode,
                        contentDescription = "Toggle Dark Mode",
                        tint = textColor
                    )
                }
                IconButton(onClick = { showTableOfContents = true }) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Daftar Isi",
                        tint = textColor
                    )
                }
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { showSettings = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Pengaturan",
                        tint = textColor
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                !fileUrl.isNullOrBlank() -> {
                    Column(Modifier.fillMaxSize()) {
                        bookInfo?.let { book ->
                            Text(
                                text = book.judul,
                                modifier = Modifier.padding(16.dp),
                                fontFamily = fraunces,
                                fontSize = 18.sp,
                                color = textColor
                            )
                        }
                        AndroidView(
                            factory = { ctx ->
                                WebView(ctx).apply {
                                    webViewClient = WebViewClient()
                                    val webSettings = this.settings
                                    @Suppress("SetJavaScriptEnabled")
                                    webSettings.javaScriptEnabled = true
                                    webSettings.builtInZoomControls = true
                                    webSettings.displayZoomControls = false
                                    loadUrl(fileUrl!!)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                        )
                    }
                }
                else -> bookContent?.let { content ->
                    val currentChapter = content.chapters.getOrNull(currentChapterIndex)
                    currentChapter?.let { chapter ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = "${currentChapterIndex + 1} dari ${content.chapters.size} • ${chapter.title}",
                                fontSize = 12.sp,
                                fontFamily = ibmplexsans,
                                color = textColor.copy(alpha = 0.6f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            Text(
                                text = chapter.title,
                                fontSize = (settings.fontSize + 4).sp,
                                fontFamily = fraunces,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = textColor,
                                lineHeight = (settings.fontSize + 4).sp * settings.lineSpacing
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = chapter.content,
                                fontSize = settings.fontSize.sp,
                                fontFamily = ibmplexsans,
                                color = textColor,
                                lineHeight = settings.fontSize.sp * settings.lineSpacing,
                                textAlign = TextAlign.Justify
                            )
                            if (content.chapters.size > 1) {
                                Spacer(modifier = Modifier.height(48.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Button(
                                        onClick = { viewModel.previousChapter() },
                                        enabled = currentChapterIndex > 0
                                    ) {
                                        Text("← Sebelumnya", fontFamily = ibmplexsans)
                                    }
                                    Button(
                                        onClick = { viewModel.nextChapter() },
                                        enabled = currentChapterIndex < content.chapters.size - 1
                                    ) {
                                        Text("Selanjutnya →", fontFamily = ibmplexsans)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showTableOfContents) {
        TableOfContentsSheet(
            chapters = bookContent?.chapters ?: emptyList(),
            currentChapterIndex = currentChapterIndex,
            onChapterSelected = { index ->
                viewModel.goToChapter(index)
                showTableOfContents = false
            },
            onDismiss = { showTableOfContents = false },
            isDarkMode = settings.isDarkMode
        )
    }

    if (showSettings) {
        ReaderSettingsSheet(
            fontSize = settings.fontSize,
            lineSpacing = settings.lineSpacing,
            onFontSizeChange = { viewModel.updateFontSize(it) },
            onLineSpacingChange = { viewModel.updateLineSpacing(it) },
            onDismiss = { showSettings = false },
            isDarkMode = settings.isDarkMode
        )
    }
}
