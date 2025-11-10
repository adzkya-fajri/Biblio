package com.example.biblio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
/**
 * A simple [Fragment] subclass.
 * Use the [FirstFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. Inflate layout dulu
        val view = inflater.inflate(R.layout.fragment_first, container, false)

        // 2. Baru findViewById dari view yang sudah di-inflate
        val sectionRecyclerView = view.findViewById<RecyclerView>(R.id.sectionRecyclerView)
        sectionRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        sectionRecyclerView.adapter = SectionAdapter(generateData())

        // 3. Return view
        return view
    }

    private fun generateData(): List<Section> {
        val sampleBooks = List(5) { Book(R.drawable.sample_cover) }
        return listOf(
            Section("Novel", sampleBooks),
            Section("Sains & Teknologi", sampleBooks),
            Section("Bisnis", sampleBooks)
        )
    }
}