package com.example.todolistapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.TaskManager.completedTasks
import com.example.todolistapp.TaskManager.pendingTasks

class PendingTaskAdapter(private val pendingTasks: MutableList<AddTaskModel>,private val adapter: ViewPagerFragmentAdapter) :
    RecyclerView.Adapter<PendingTaskAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val taskTitleDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dueDate: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_task_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = pendingTasks[position]
        holder.taskTitle.text = currentItem.title
        holder.taskTitleDescription.text = currentItem.description // Set empty string if null
        holder.dueDate.text = currentItem.dueDate // Set empty string if null

        // Handle optional description
        if (currentItem.description != null) {
            holder.taskTitleDescription.visibility = View.VISIBLE
        } else {
            holder.taskTitleDescription.visibility = View.GONE
        }

        // Handle optional due date
        if (currentItem.dueDate != null) {
            holder.dueDate.visibility = View.VISIBLE
        } else {
            holder.dueDate.visibility = View.GONE
        }

        // Set the Check Box
        holder.checkbox.isChecked = false // Initially unchecked

        holder.checkbox.setOnClickListener {
            val task = pendingTasks[position]
            if (holder.checkbox.isChecked) {
                moveTaskToCompleted(task, position)
            }
        }
    }

    override fun getItemCount(): Int {
        return pendingTasks.size
    }

    // Function to move a task to the completed list
    @SuppressLint("NotifyDataSetChanged")
    private fun moveTaskToCompleted(task: AddTaskModel, position: Int) {
        // Remove from pendingTasks
        pendingTasks.removeAt(position)
        notifyItemRemoved(position)

        // Add to completedTasks
        completedTasks.add(task)
        notifyItemInserted(completedTasks.size - 1)

        // Update the completed fragment's RecyclerView
        // Assuming that the "Completed" tab is at index 1 in the ViewPager
        // Update the adapter with the new list of tasks
        (adapter.fragments[1] as completedFragment).recyclerView.adapter?.notifyDataSetChanged()
    }
    }