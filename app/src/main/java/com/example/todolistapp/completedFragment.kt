package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class completedFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var completedTaskAdapter: CompletedTaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed, container, false)
        recyclerView = view.findViewById(R.id.pending_task_recycler_view)
        completedTaskAdapter = CompletedTaskAdapter(TaskManager.completedTasks, AppManager.adapter) // Pass the adapter
        recyclerView.adapter = completedTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        return view
    }
}