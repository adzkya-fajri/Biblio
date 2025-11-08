package com.example.biblio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

data class Book(val imageRes: Int)
data class Section(val title: String, val books: List<Book>)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sectionRecyclerView = findViewById<RecyclerView>(R.id.sectionRecyclerView)
        sectionRecyclerView.layoutManager = LinearLayoutManager(this)
        sectionRecyclerView.adapter = SectionAdapter(generateData())
    }

    private fun generateData(): List<Section> {
        val sampleBooks = List(5) { Book(R.drawable.sample_cover) }
        return listOf(
            Section("novel", sampleBooks),
            Section("sains & teknologi", sampleBooks),
            Section("bisnis", sampleBooks)
        )
    }
}
