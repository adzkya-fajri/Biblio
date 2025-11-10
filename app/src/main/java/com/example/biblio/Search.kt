package com.example.biblio

import Book
import Search_BookAdapter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Search : AppCompatActivity() {

    private lateinit var Search_CategoryAdapter: Search_CategoryAdapter
    private lateinit var Search_BookAdapter: Search_BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupCategories()
        setupSections()
    }

    private fun setupCategories() {
        val recycler = findViewById<RecyclerView>(R.id.recyclerCategories)
        recycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        val categories = listOf("Fiksi", "Motivasi", "Alam", "Karya Sastra", "Indie")
        Search_CategoryAdapter = Search_CategoryAdapter(categories)
        recycler.adapter = Search_CategoryAdapter
    }

    private fun setupSections() {
        val sections = listOf(
            Triple(R.id.sectionPopular, "Populer", generateDummyBooks()),
            Triple(R.id.sectionRecommended, "Buku ini kamu banget!", generateDummyBooks()),
            Triple(R.id.sectionNonFiction, "Non-fiksi", generateDummyBooks())
        )

        for ((id, title, books) in sections) {
            val sectionView = findViewById<View>(id)
            val titleView = sectionView.findViewById<TextView>(R.id.sectionTitle)
            val recycler = sectionView.findViewById<RecyclerView>(R.id.recyclerBooks)

            titleView.text = title
            recycler.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
            recycler.adapter = Search_BookAdapter(books)
        }
    }

    private fun generateDummyBooks(): List<Book> {
        return List(6) {
            Book("Lorem Ipsum", "Lorem Ipsum", R.drawable.image10)
        }
    }
}
