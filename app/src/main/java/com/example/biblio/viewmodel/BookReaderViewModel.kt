package com.example.biblio.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biblio.data.model.Buku
import com.example.biblio.data.repository.BukuRepository
import com.example.biblio.data.repository.ProfileRepository
import com.example.biblio.data.repository.ReadingProgressRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.readium.adapter.pdfium.document.PdfiumDocumentFactory
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.getOrElse
import org.readium.r2.shared.util.http.DefaultHttpClient
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.streamer.parser.epub.EpubParser
import java.io.File
import java.net.URL

data class ReaderSettings(
    val isDarkMode: Boolean = false,
    val fontSize: Int = 16,
    val lineSpacing: Float = 1.5f
)

class BookReaderViewModel(
    application: Application,
    private val bukuRepository: BukuRepository,
    private val progressRepository: ReadingProgressRepository,
    private val profileRepository: ProfileRepository
) : AndroidViewModel(application) {


    private var currentBookId: String? = null
    val currentLocator = MutableStateFlow<Locator?>(null)

    val readingProgress = MutableStateFlow<Pair<Int, Int>?>(null) // page to total

    @OptIn(ExperimentalReadiumApi::class)
    fun observeLocator(locatorFlow: StateFlow<Locator>) {
        viewModelScope.launch {
            locatorFlow.collect { locator ->
                onLocatorChanged(locator)
            }
        }
    }

    // Panggil ini saat locator berubah (dari listener)
    fun onLocatorChanged(locator: Locator) {
        val page = locator.locations.position ?: 0
        val total = publication.value?.readingOrder?.size ?: 1
        readingProgress.value = Pair(page, total)

        // simpan ke Room
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

            // 1. Cek apakah sudah ada di cache
            val cachedFile = getCachedFile(book.id)
            if (cachedFile != null && !forceRefresh) {
                Log.d("ReaderVM", "Loading from cache: ${cachedFile.path}")
                openPublication(cachedFile)
                _isLoading.value = false
                return@launch
            }

            // 2. Jika tidak ada di cache, cek apakah user premium
            val user = profileRepository.getCachedProfile()
            val isSubscribed = user?.isSubscribed == true

            if (!isSubscribed) {
                _error.value = "Berlangganan untuk membaca buku ini secara offline."
                _isLoading.value = false
                return@launch
            }

            // 3. Jika premium, download
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

    fun toggleDarkMode() {
        _settings.value = _settings.value.copy(
            isDarkMode = !_settings.value.isDarkMode
        )
    }

    private suspend fun downloadToCache(urlString: String, bookId: String): File {
        return withContext(Dispatchers.IO) {
            val url = URL(urlString)
            val ext = if (urlString.contains(".epub")) "epub" else "pdf"
            val cacheFile = File(getApplication<Application>().cacheDir, "$bookId.$ext")

            if (cacheFile.exists()) {
                Log.d("ReaderVM", "Using cache: ${cacheFile.path}, size: ${cacheFile.length()}")
                if (cacheFile.length() == 0L) {
                    Log.d("ReaderVM", "Cache corrupted, re-downloading")
                    cacheFile.delete()
                } else {
                    return@withContext cacheFile
                }
            }

            Log.d("ReaderVM", "Downloading: $urlString")
            url.openStream().use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("ReaderVM", "Downloaded: ${cacheFile.length()} bytes")
            cacheFile
        }
    }

    fun updateFontSize(size: Int) {
        _settings.value = _settings.value.copy(fontSize = size)
    }

    fun updateLineSpacing(spacing: Float) {
        _settings.value = _settings.value.copy(lineSpacing = spacing)
    }

    override fun onCleared() {
        super.onCleared()
        _publication.value?.close()
    }
}
