package com.example.biblio.data.repository

import android.content.Context
import com.example.biblio.data.model.BookContent
import com.example.biblio.data.model.Chapter
import kotlinx.serialization.json.Json

class BookContentRepository(private val context: Context) {
    private val json = Json { ignoreUnknownKeys = true }

    // Load konten buku dari JSON (jika sudah ada)
    fun loadBookContent(bookId: String): BookContent? {
        return try {
            val fileName = "book_content_$bookId.json"
            val jsonString = context.assets.open(fileName)
                .bufferedReader()
                .use { it.readText() }
            json.decodeFromString<BookContent>(jsonString)
        } catch (e: Exception) {
            // Jika file tidak ada, generate dummy content
            generateDummyContent(bookId)
        }
    }

    // Generate dummy content untuk demo
    private fun generateDummyContent(bookId: String): BookContent {
        val chapters = List(10) { chapterIndex ->
            Chapter(
                id = chapterIndex + 1,
                title = "Chapter ${chapterIndex + 1}: Lorem Ipsum",
                content = generateLoremIpsum(chapterIndex)
            )
        }
        return BookContent(bookId, chapters)
    }

    private fun generateLoremIpsum(seed: Int): String {
        val paragraphs = listOf(
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean quis ultrices vitae metus ac, aliquet porta leo. Donec at enim dictum mauris fringilla lobortis. Cras nunc risus, rhoncus sed libero ac, pharetra semper enim. Nunc vitae felis turpis. Fusce sit amet massa ex. Nulla facilisi. Orci varius neque ante et magnis dis parturient montes, nascetur ridiculus mus. Sed eget dui eu diam efficitur feugiat. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; Integer dictum nibh nec neque porttitor porttitor. Aliquam erat volutpat.",

            "Ut vel varius purus. Maecenas mollis purus vel nibh fringilla mattis. Nam sit amet elit id metus euismod quisque. Suspendisse scelerisque nibh massa, sit amet vestibulum sem condimentum sed. Proin leo leo, bibendum vitae blandit vel, viverra quis nibh. Integer congue ex tellus, vel euismod sed lobortis in. Etiam in metus vitae nunc gravida ullamcorper vel ac tortor. Donec sapien nisl, suscipit at est. Phasellus faucibus nulla. Integer hendrerit mauris sed mi mollis, in dignissim sapien vehicula. Suspendisse dapibus tortor. Proin hendrerit nisi commodo nunc tristique fermentum. Cras sit amet ullamcorper lorem. In sit amet lorem vel turpis consectetur egestas. Praesent arcu ipsum, blandit eu mi eget, tincidunt varius sem. Vivamus ornare commodo lacus nec adipiscing.",

            "Etiam scelerisque leo elit, ut ullamcorper urna suscipit vel. Fusce ut fermentum tempor libero, nec porta leo phoncus sed. Pellentesque a efficitur justo. Maecenas ac sapien ultricies, convallis sapien ut, euismod erat. Vestibulum a elit semper lorem suscipit volutpat non vel ante. Sed vel tortor vehicula orci aliquet posuere quis vel magna.",

            "Donec sit amet dui at risus efficitur scelerisque. Pellentesque hendrerit lectus id mauris ornare, vel tincidunt urna tincidunt. Sed malesuada nibh vitae lectus lobortis, at fermentum risus venenatis. Nulla facilisi. Vivamus sollicitudin neque sed sapien faucibus, id consectetur orci congue. Proin ultricies magna sit amet dui commodo, non euismod nunc tincidunt."
        )

        // Generate 3-5 paragraphs per chapter
        val numParagraphs = 3 + (seed % 3)
        return (0 until numParagraphs)
            .joinToString("\n\n") { paragraphs[it % paragraphs.size] }
    }
}