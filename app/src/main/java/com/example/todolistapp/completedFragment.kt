package com.example.todolistapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class completedFragment : Fragment() {

    private lateinit var completedTaskAdapter: CompletedTaskAdapter
    lateinit var recyclerView: RecyclerView
    private lateinit var completedTasks: MutableList<AddTaskModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed, container, false)
        recyclerView = view.findViewById(R.id.pending_task_recycler_view)

        completedTasks = mutableListOf(
            AddTaskModel("Morning Workout", "30 minutes of cardio", "Today"),
            AddTaskModel("Task 2", "Description 2", null),
            AddTaskModel("Task 3", null, "2024-02-15"))

        // Get the completedTasks list from MainActivity
        completedTaskAdapter = CompletedTaskAdapter(completedTasks)
        recyclerView.adapter = completedTaskAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        return view
    }
}