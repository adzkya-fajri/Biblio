package com.example.biblio.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.model.Buku
import com.example.biblio.di.AppModule
import com.example.biblio.ibmplexsans
import com.example.biblio.ui.components.ImmersiveMode
import com.example.biblio.ui.components.ReaderSettingsSheet
import com.example.biblio.viewmodel.BookReaderViewModel
import com.example.biblio.viewmodel.BookReaderViewModelFactory
import com.example.biblio.viewmodel.ReaderTheme
import com.example.biblio.viewmodel.ReadingProgressViewModel
import kotlinx.serialization.json.Json
import org.readium.adapter.pdfium.navigator.PdfiumEngineProvider
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.pdf.PdfNavigatorFactory
import org.readium.r2.navigator.pdf.PdfNavigatorFragment
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class, ExperimentalAnimationApi::class)
@Composable
fun BookReaderScreen(
    bookJson: String,
    navController: NavController,
) {
    ImmersiveMode()

    val context = LocalContext.current
    val viewModel: BookReaderViewModel = viewModel(
        factory = BookReaderViewModelFactory(
            context.applicationContext as Application,
            AppModule.provideBukuRepository(context),
            AppModule.provideReadingProgressRepository(context),
            AppModule.provideProfileRepository(context),
            AppModule.provideReaderPreferencesManager(context)
        )
    )

    // Decode dan parse book object
    val book = remember(bookJson) {
        try {
            val decoded = URLDecoder.decode(bookJson, "UTF-8")
            Json.decodeFromString<Buku>(decoded)
        } catch (e: Exception) {
            null
        }
    }

    val activity = LocalActivity.current!!
    val fm = (activity as FragmentActivity).supportFragmentManager
    val readingStateViewModel: ReadingProgressViewModel = viewModel(
        viewModelStoreOwner = activity as ViewModelStoreOwner
    )
    val progress by viewModel.readingProgress.collectAsState()
    val savedLocator by viewModel.currentLocator.collectAsState()

    var showControls by remember { mutableStateOf(false) }
    var showChapters by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    // Update progress ke shared ViewModel segera setelah locator tersedia atau saat book berubah
    LaunchedEffect(savedLocator, book) {
        val b = book ?: return@LaunchedEffect
        val page = savedLocator?.locations?.position ?: 0
        readingStateViewModel.updateProgress(b, page)
    }

    // Efek saat keluar dari reader: sinkronisasi ke remote DAN bersihkan fragment
    DisposableEffect(Unit) {
        onDispose {
            readingStateViewModel.syncWithRemote()
            
            // Bersihkan fragment dari Activity's FragmentManager agar tidak crash saat recreation
            val fragment = fm.findFragmentByTag("navigator")
            if (fragment != null) {
                try {
                    fm.beginTransaction().remove(fragment).commitAllowingStateLoss()
                } catch (e: Exception) {
                    Log.e("BookReader", "Error removing fragment", e)
                }
            }
        }
    }

    LaunchedEffect(book) {
        book?.let { viewModel.loadBook(it) }
    }

// Di BookReaderScreen - LaunchedEffect progress
    LaunchedEffect(progress, book) {
        Log.d("ReadingBar", "progress=$progress, book=$book")
        progress?.let { (page, _) ->
            book?.let {
                Log.d("ReadingBar", "updateProgress page=$page, bookPage=${it.page}")
                readingStateViewModel.updateProgress(it, page)
            }
        }
    }

    val publication by viewModel.publication.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val isSystemDark = isSystemInDarkTheme()
    
    // Update system theme context for AUTO mode
    LaunchedEffect(isSystemDark) {
        viewModel.updateSystemTheme(isSystemDark)
    }

    // Sync UI settings to Navigator
    LaunchedEffect(settings, isSystemDark) {
        val fragment = fm.findFragmentByTag("navigator")
        if (fragment is EpubNavigatorFragment) {
            fragment.submitPreferences(viewModel.createEpubPreferences(isSystemDark))
        }
    }

    val backgroundColor = when (settings.resolvedTheme) {
        ReaderTheme.DARK -> Color(0xFF1A1A1A)
        ReaderTheme.SEPIA -> Color(0xFFF4ECD8)
        else -> colorResource(R.color.colorBackground)
    }

    val textColor = if (settings.isDarkMode) Color(0xFFE0E0E0) else colorResource(R.color.colorOnBackground)
    val surfaceColor = when (settings.resolvedTheme) {
        ReaderTheme.DARK -> Color(0xFF2A2A2A)
        ReaderTheme.SEPIA -> Color(0xFFE8DDC0)
        else -> colorResource(R.color.colorSurface)
    }

    Scaffold(
        containerColor = backgroundColor,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = error!!, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { book?.let { viewModel.loadBook(it) } }) {
                        Text("Coba Lagi")
                    }
                }
            } else {
                publication?.let { pub ->
                    val savedLocator by viewModel.currentLocator.collectAsState()

                    key(pub) {
                        AndroidView(
                            factory = { ctx ->
                            val frameLayout = android.widget.FrameLayout(ctx).apply {
                                id = R.id.reader_fragment_container
                            }
                            val activity = ctx as? FragmentActivity
                            val fm = activity?.supportFragmentManager
                            val isPdf = pub.conformsTo(Publication.Profile.PDF)

                            val listener = object : EpubNavigatorFragment.Listener {
                                override fun onExternalLinkActivated(url: AbsoluteUrl) {}
                            }

                            val fragment = if (isPdf) {
                                val factory = PdfNavigatorFactory(pub, PdfiumEngineProvider())
                                val fragmentFactory = factory.createFragmentFactory(
                                    initialLocator = savedLocator,
                                    listener = null
                                )
                                fm?.fragmentFactory = fragmentFactory
                                fragmentFactory.instantiate(ctx.classLoader, PdfNavigatorFragment::class.java.name)
                            } else {
                                val factory = EpubNavigatorFactory(pub)
                                val fragmentFactory = factory.createFragmentFactory(
                                    initialLocator = savedLocator,
                                    listener = listener
                                )
                                fm?.fragmentFactory = fragmentFactory
                                fragmentFactory.instantiate(ctx.classLoader, EpubNavigatorFragment::class.java.name)
                            }

                            // Delay commit sampai view attach ke window
                            frameLayout.post {
                                fragment.let { f ->
                                    val currentActivity = frameLayout.context as? FragmentActivity
                                    if (currentActivity != null && !currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                        fm?.beginTransaction()
                                            ?.replace(frameLayout.id, f, "navigator")
                                            ?.commitAllowingStateLoss()

                                        (f as? org.readium.r2.navigator.VisualNavigator)?.let { nav ->
                                            viewModel.observeLocator(nav.currentLocator)
                                            
                                            nav.addInputListener(object : org.readium.r2.navigator.input.InputListener {
                                                override fun onTap(event: org.readium.r2.navigator.input.TapEvent): Boolean {
                                                    showControls = !showControls
                                                    return true
                                                }
                                            })
                                        }
                                    }
                                }
                            }

                            frameLayout
                        },
                        modifier = Modifier
                            .fillMaxSize()
                    )
                }

                    // OVERLAY CONTROLS
                    AnimatedVisibility(
                        visible = showControls,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { -it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it }),
                        modifier = Modifier.align(Alignment.TopCenter)
                    ) {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = book?.title ?: "",
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = textColor
                                    )
                                    Text(
                                        text = book?.author ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        color = textColor.copy(alpha = 0.7f)
                                    )
                                }
                            },
                            navigationIcon = {
                                IconButton(onClick = { navController.navigateUp() }) {
                                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = textColor)
                                }
                            },
                            actions = {
                                IconButton(onClick = { viewModel.toggleDarkMode() }) {
                                    Icon(
                                        imageVector = if (settings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                                        contentDescription = "Toggle Dark Mode",
                                        tint = textColor
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = backgroundColor.copy(alpha = 0.95f)
                            )
                        )
                    }

                    AnimatedVisibility(
                        visible = showControls,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        Surface(
                            color = backgroundColor.copy(alpha = 0.95f),
                            tonalElevation = 4.dp
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    IconButton(onClick = { showChapters = true }) {
                                        Icon(Icons.AutoMirrored.Filled.List, "Chapters", tint = textColor)
                                    }
                                    
                                    val currentPage = progress?.first ?: 0
                                    val totalPages = progress?.second ?: 0
                                    Text(
                                        text = if (totalPages > 0) "Halaman $currentPage dari $totalPages" else "Memuat...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = textColor
                                    )

                                    IconButton(onClick = { showSettings = true }) {
                                        Icon(Icons.Default.Settings, "Settings", tint = textColor)
                                    }
                                }
                            }
                        }
                    }

                    // CHAPTERS SHEET
                    if (showChapters) {
                        ModalBottomSheet(
                            onDismissRequest = { showChapters = false },
                            containerColor = surfaceColor,
                            contentColor = textColor
                        ) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 32.dp)
                            ) {
                                item {
                                    Text(
                                        "Daftar Isi",
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(16.dp),
                                        fontFamily = ibmplexsans,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                items(pub.tableOfContents) { link ->
                                    ListItem(
                                        headlineContent = { Text(link.title ?: "Tanpa Judul", color = textColor) },
                                        modifier = Modifier.clickable {
                                            val fragment = fm.findFragmentByTag("navigator") as? org.readium.r2.navigator.Navigator
                                            fragment?.go(link)
                                            showChapters = false
                                            showControls = false
                                        },
                                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                                    )
                                }
                            }
                        }
                    }

                    // SETTINGS SHEET
                    if (showSettings) {
                        ReaderSettingsSheet(
                            currentTheme = settings.theme,
                            currentFontSize = settings.fontSize,
                            onThemeChange = { viewModel.updateTheme(it) },
                            onFontSizeChange = { viewModel.updateFontSize(it) },
                            onDismiss = { showSettings = false }
                        )
                    }
                }
            }
        }
    }
}
