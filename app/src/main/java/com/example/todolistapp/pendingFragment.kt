package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class pendingFragment : Fragment() {

    private lateinit var pendingTaskAdapter: PendingTaskAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending, container, false)
        recyclerView = view.findViewById(R.id.pending_task_recycler_view)



        // Get the pendingTasks list from MainActivity (make sure it's accessible)
        pendingTaskAdapter = PendingTaskAdapter(TaskManager.pendingTasks)
        recyclerView.adapter = pendingTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }
}