package com.example.biblio.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.data.repository.ReadingProgressRepository
import com.example.biblio.data.preferences.ReaderPreferencesManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.navigator.preferences.Theme
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import java.io.File
import java.net.URL

enum class ReaderTheme {
    LIGHT, DARK, SEPIA, AUTO
}

data class ReaderSettings(
    val theme: ReaderTheme = ReaderTheme.AUTO,
    val fontSize: Double = 1.0, // 1.0 = 100%
    val isSystemDark: Boolean = false
) {
    val resolvedTheme: ReaderTheme get() = 
        if (theme == ReaderTheme.AUTO) {
            if (isSystemDark) ReaderTheme.DARK else ReaderTheme.LIGHT
        } else theme

    val isDarkMode: Boolean get() = resolvedTheme == ReaderTheme.DARK
}

class BookReaderViewModel(
    application: Application,
    private val bukuRepository: BukuRepository,
    private val progressRepository: ReadingProgressRepository,
    private val profileRepository: ProfileRepository,
    private val readerPreferences: ReaderPreferencesManager
) : AndroidViewModel(application) {

    private var currentBookId: String? = null
    val currentLocator = MutableStateFlow<Locator?>(null)

    val readingProgress = MutableStateFlow<Pair<Int, Int>?>(null) // page to total

    private var locatorObservationJob: kotlinx.coroutines.Job? = null

    @OptIn(ExperimentalReadiumApi::class)
    fun observeLocator(locatorFlow: StateFlow<Locator>) {
        locatorObservationJob?.cancel()
        locatorObservationJob = viewModelScope.launch {
            locatorFlow.collect { locator ->
                onLocatorChanged(locator)
            }
        }
    }

    fun onLocatorChanged(locator: Locator) {
        val page = locator.locations.position ?: 0
        val total = publication.value?.readingOrder?.size ?: 1
        readingProgress.value = Pair(page, total)

        currentBookId?.let { bookId ->
            viewModelScope.launch {
                progressRepository.saveLocator(bookId, locator)
            }
        }
    }

    private val _publication = MutableStateFlow<Publication?>(null)
    val publication: StateFlow<Publication?> = _publication.asStateFlow()

    private val _settings = MutableStateFlow(ReaderSettings())
    val settings: StateFlow<ReaderSettings> = _settings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        // Load persisted settings
        combine(readerPreferences.theme, readerPreferences.fontSize) { theme, fontSize ->
            Pair(theme, fontSize)
        }.onEach { (theme, fontSize) ->
            _settings.value = _settings.value.copy(theme = theme, fontSize = fontSize)
        }.launchIn(viewModelScope)
    }

    fun updateSystemTheme(isDark: Boolean) {
        _settings.value = _settings.value.copy(isSystemDark = isDark)
    }

    private val httpClient = DefaultHttpClient()
    private val assetRetriever = AssetRetriever(application.contentResolver, httpClient)
    private val publicationOpener = PublicationOpener(
        DefaultPublicationParser(
            application,
            httpClient,
            assetRetriever,
            pdfFactory = PdfiumDocumentFactory(application)
        )
    )

    fun loadBook(book: Buku, forceRefresh: Boolean = false) {
        currentBookId = book.id
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            currentLocator.value = progressRepository.getLocator(book.id)

            val cachedFile = getCachedFile(book.id)
            if (cachedFile != null && !forceRefresh) {
                Log.d("ReaderVM", "Loading from cache: ${cachedFile.path}")
                openPublication(cachedFile)
                _isLoading.value = false
                return@launch
            }

            val user = profileRepository.getCachedProfile()
            val isSubscribed = user?.isSubscribed == true

            if (!isSubscribed) {
                _error.value = "Berlangganan untuk membaca buku ini secara offline."
                _isLoading.value = false
                return@launch
            }

            bukuRepository.downloadBook(book.id).fold(
                onSuccess = { urlString ->
                    try {
                        val cacheFile = downloadToCache(urlString, book.id)
                        openPublication(cacheFile)
                    } catch (e: Exception) {
                        Log.e("ReaderVM", "Error: ${e.message}")
                        _error.value = "Gagal membuka buku: ${e.localizedMessage}"
                    }
                },
                onFailure = {
                    _error.value = "Gagal mengunduh buku: ${it.localizedMessage}"
                }
            )
            _isLoading.value = false
        }
    }

    private suspend fun openPublication(file: File) {
        try {
            val url = AbsoluteUrl(file.toURI().toString())!!
            val asset = assetRetriever.retrieve(url).getOrElse {
                throw Exception("Failed to retrieve: $it")
            }
            val publication = publicationOpener.open(asset, allowUserInteraction = false).getOrElse {
                throw Exception("Failed to open: $it")
            }
            _publication.value = publication
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getCachedFile(bookId: String): File? {
        val cacheDir = getApplication<Application>().cacheDir
        val epub = File(cacheDir, "$bookId.epub")
        if (epub.exists() && epub.length() > 0) return epub
        val pdf = File(cacheDir, "$bookId.pdf")
        if (pdf.exists() && pdf.length() > 0) return pdf
        return null
    }

    private suspend fun downloadToCache(urlString: String, bookId: String): File {
        return withContext(Dispatchers.IO) {
            val url = URL(urlString)
            val ext = if (urlString.contains(".epub")) "epub" else "pdf"
            val cacheFile = File(getApplication<Application>().cacheDir, "$bookId.$ext")

            if (cacheFile.exists()) {
                if (cacheFile.length() == 0L) {
                    cacheFile.delete()
                } else {
                    return@withContext cacheFile
                }
            }

            url.openStream().use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            cacheFile
        }
    }

    fun updateTheme(theme: ReaderTheme) {
        viewModelScope.launch {
            readerPreferences.setTheme(theme)
        }
    }

    fun updateFontSize(size: Double) {
        viewModelScope.launch {
            readerPreferences.setFontSize(size)
        }
    }

    fun toggleDarkMode() {
        val currentResolved = settings.value.resolvedTheme
        val nextTheme = if (currentResolved == ReaderTheme.DARK) ReaderTheme.LIGHT else ReaderTheme.DARK
        updateTheme(nextTheme)
    }

    @OptIn(ExperimentalReadiumApi::class)
    fun createEpubPreferences(isSystemDark: Boolean): EpubPreferences {
        val readiumTheme = when (_settings.value.theme) {
            ReaderTheme.LIGHT -> Theme.LIGHT
            ReaderTheme.DARK -> Theme.DARK
            ReaderTheme.SEPIA -> Theme.SEPIA
            ReaderTheme.AUTO -> if (isSystemDark) Theme.DARK else Theme.LIGHT
        }
        return EpubPreferences(
            theme = readiumTheme,
            fontSize = _settings.value.fontSize
        )
    }

    override fun onCleared() {
        super.onCleared()
        _publication.value?.close()
    }
}
