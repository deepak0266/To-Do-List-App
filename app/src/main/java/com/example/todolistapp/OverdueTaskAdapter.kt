package com.example.todolistapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OverdueTaskAdapter(private val overdueTasks: MutableList<AddTaskModel>) :
    RecyclerView.Adapter<OverdueTaskAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val taskTitleDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dueDate: TextView = itemView.findViewById(R.id.dateTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.overdue_task_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = overdueTasks[position]
        holder.taskTitle.text = currentItem.title
        holder.taskTitleDescription.text = currentItem.description ?: ""
        holder.dueDate.text = currentItem.dueDate ?: ""

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
    }

    override fun getItemCount(): Int {
        return overdueTasks.size
    }
}