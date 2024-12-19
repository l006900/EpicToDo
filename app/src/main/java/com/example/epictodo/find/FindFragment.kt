package com.example.epictodo.find

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.epictodo.R
import com.example.epictodo.find.add.FindAddActivity
import com.example.epictodo.find.vm.FindAdapter
import com.example.epictodo.find.vm.FindViewModel

class FindFragment : Fragment() {

    private lateinit var viewModel: FindViewModel
    private lateinit var adapter: FindAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var findAdd: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_find, container, false)

        recyclerView = view.findViewById(R.id.find_recycler)
        findAdd = view.findViewById(R.id.find_search)

        setupRecyclerView()
        setupViewModel()
        setupListeners()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = FindAdapter()
        recyclerView.adapter = adapter
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(FindViewModel::class.java)
        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            adapter.setFindEntities(items)
        }
    }

    private fun setupListeners() {
        findAdd.setOnClickListener {
            val intent = Intent(context, FindAddActivity::class.java)
            startActivity(intent)
        }
    }
}