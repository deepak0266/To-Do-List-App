package com.example.todolistapp

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.TaskManager.completedTasks
import com.example.todolistapp.TaskManager.pendingTasks

class CompletedTaskAdapter(
    private val completedTasks: MutableList<AddTaskModel>,
    private val adapter: ViewPagerFragmentAdapter
) : RecyclerView.Adapter<CompletedTaskAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val taskTitleDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dueDate: TextView = itemView.findViewById(R.id.dateTextView)
        val llrow: LinearLayout = itemView.findViewById(R.id.ll1) // Get the LinearLayout reference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.completed_task_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = completedTasks[position]
        holder.taskTitle.text = currentItem.title
        holder.taskTitleDescription.text = currentItem.description
        holder.dueDate.text = currentItem.dueDate

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

        // Long-press for Edit/Delete
        holder.llrow.setOnLongClickListener { // Set the listener on the LinearLayout
            showMoveDeleteDialog(holder.itemView.context, position)
            true
        }
    }

    override fun getItemCount(): Int {
        return completedTasks.size
    }

    // Dialog for moving a task back to pending or deleting it
    private fun showMoveDeleteDialog(context: Context, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.move_task_and_delete)

        val taskTitleTextView = dialog.findViewById<TextView>(R.id.dialog_task_title_text)
        val moveToPendingButton = dialog.findViewById<Button>(R.id.move_to_pending_button)
        val deleteButton = dialog.findViewById<Button>(R.id.delete_button)

        // Populate the dialog with the existing task data
        val task = completedTasks[position]
        taskTitleTextView.text = task.title

        // Move task back to pending
        moveToPendingButton.setOnClickListener {
            // Remove the task from the completedTasks list
            val taskToRemove = completedTasks[position]
            completedTasks.removeAt(position)
            notifyItemRemoved(position)

            // Add the task to the pendingTasks list
            pendingTasks.add(taskToRemove)

            // Update the pending fragment's RecyclerView
            (AppManager.adapter as? ViewPagerFragmentAdapter)?.fragments?.get(0)?.view?.findViewById<RecyclerView>(R.id.pending_task_recycler_view)?.adapter?.notifyItemInserted(pendingTasks.size - 1)

            dialog.dismiss()
        }

        // Delete task
        deleteButton.setOnClickListener {
            completedTasks.removeAt(position)
            notifyItemRemoved(position)
            dialog.dismiss()
        }

        dialog.show()
    }
}