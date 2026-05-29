package com.example.biblio.ui.screens

import android.app.Application
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.biblio.R
import com.example.biblio.data.model.Buku
import com.example.biblio.di.AppModule
import com.example.biblio.ui.components.ImmersiveMode
import com.example.biblio.viewmodel.BookReaderViewModel
import com.example.biblio.viewmodel.BookReaderViewModelFactory
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalReadiumApi::class)
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
            AppModule.provideProfileRepository(context)
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

    val backgroundColor = if (settings.isDarkMode) Color(0xFF1A1A1A) else colorResource(R.color.colorBackground)
    val textColor = if (settings.isDarkMode) Color(0xFFE0E0E0) else colorResource(R.color.colorOnBackground)

    Scaffold(
        containerColor = backgroundColor,
//        bottomBar = {
//            BottomAppBar(
//                containerColor = if (settings.isDarkMode) Color(0xFF2A2A2A) else colorResource(R.color.colorBackground),
//            ) {
//                // BACK BUTTON
//                IconButton(onClick = { navController.navigateUp() }) {
//                    Icon(
//                        painter = painterResource(R.drawable.arrow_back_24px),
//                        contentDescription = "Kembali",
//                        tint = textColor
//                    )
//                }
//
//                // DARK MODE TOGGLE
//                IconButton(onClick = { viewModel.toggleDarkMode() }) {
//                    Icon(
//                        imageVector = if (settings.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
//                        contentDescription = "Toggle Dark Mode",
//                        tint = textColor
//                    )
//                }
//
//                Spacer(Modifier.weight(1f))
//
//                // MORE OPTIONS
//                IconButton(onClick = { /* showSettings = true */ }) {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = "Pengaturan",
//                        tint = textColor
//                    )
//                }
//            }
//        }
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
                                fragment?.let {
                                    val currentActivity = frameLayout.context as? FragmentActivity
                                    if (currentActivity != null && !currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                        fm?.beginTransaction()
                                            ?.replace(frameLayout.id, it, "navigator")
                                            ?.commitAllowingStateLoss()

                                        (it as? org.readium.r2.navigator.Navigator)?.let { nav ->
                                            viewModel.observeLocator(nav.currentLocator)
                                        }
                                    }
                                }
                            }

                            frameLayout
                        },
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}
