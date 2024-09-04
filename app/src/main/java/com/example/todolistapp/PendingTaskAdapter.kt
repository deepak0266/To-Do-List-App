package com.example.todolistapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistapp.TaskManager.completedTasks
import com.example.todolistapp.TaskManager.pendingTasks
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PendingTaskAdapter(
    private val pendingTasks: MutableList<AddTaskModel>,
    private val adapter: ViewPagerFragmentAdapter
) : RecyclerView.Adapter<PendingTaskAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val taskTitle: TextView = itemView.findViewById(R.id.taskTitleTextView)
        val taskTitleDescription: TextView = itemView.findViewById(R.id.descriptionTextView)
        val dueDate: TextView = itemView.findViewById(R.id.dateTextView)
        val llrow: LinearLayout = itemView.findViewById(R.id.ll1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pending_task_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = pendingTasks[position]
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

        holder.checkbox.isChecked = false

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val task = pendingTasks[position]
                moveTaskToCompleted(task, position)
            }
        }

        // Long-press for Edit/Delete
        holder.llrow.setOnLongClickListener {
            showEditDeleteDialog(holder.itemView.context, position)
            true
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
        (AppManager.adapter as? ViewPagerFragmentAdapter)?.fragments?.get(1)?.view?.findViewById<RecyclerView>(R.id.pending_task_recycler_view)?.adapter?.notifyDataSetChanged()

        // Update the pending task's checkbox
        notifyItemChanged(position) // Update the visual state of the checkbox
    }

    // Dialog for editing or deleting a task
    private fun showEditDeleteDialog(context: Context, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.edit_delete_dialog)

        val taskTitleEditText = dialog.findViewById<EditText>(R.id.edit_task_title)
        val taskDescriptionEditText = dialog.findViewById<EditText>(R.id.edit_task_description)
        val dueDateEditText = dialog.findViewById<EditText>(R.id.edit_due_date)
        val saveButton = dialog.findViewById<Button>(R.id.save_button)
        val deleteButton = dialog.findViewById<Button>(R.id.delete_button)

        // Populate the dialog with the existing task data
        val task = pendingTasks[position]
        taskTitleEditText.setText(task.title)
        taskDescriptionEditText.setText(task.description)
        dueDateEditText.setText(task.dueDate)

        // Set up the DatePickerDialog for the due date
        dueDateEditText.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                    // Format the selected date
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val formattedDate = dateFormat.format(selectedDate.time)
                    dueDateEditText.setText(formattedDate)
                },
                year,
                month,
                day
            )

            // Only allow future dates
            datePickerDialog.datePicker.minDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        // Save changes
        saveButton.setOnClickListener {
            val newTitle = taskTitleEditText.text.toString()
            val newDescription = taskDescriptionEditText.text.toString()
            val newDueDate = dueDateEditText.text.toString()

            if (newTitle.isNotEmpty()) {
                // Update the task in the list
                pendingTasks[position] = AddTaskModel(newTitle, newDescription, newDueDate)
                notifyItemChanged(position)
                dialog.dismiss()
            } else {
                taskTitleEditText.error = "Please enter a task title"
            }
        }

        // Delete task
        deleteButton.setOnClickListener {
            pendingTasks.removeAt(position)
            notifyItemRemoved(position)
            dialog.dismiss()
        }

        dialog.show()
    }
}