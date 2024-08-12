package com.example.todolistapp

import android.app.Dialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

// Singleton for Task Management
object TaskManager {
    val pendingTasks: MutableList<AddTaskModel> = mutableListOf(
        AddTaskModel("Morning Workout", "30 minutes of Abs", "Today"),
        AddTaskModel("Task 2", "Description 2", null),
        AddTaskModel("Task 3", null, "2024-02-15")
    )
    val completedTasks: MutableList<AddTaskModel> = mutableListOf()
    val overdueTasks: MutableList<AddTaskModel> = mutableListOf()
}

// App Manager Singleton
object AppManager {
    lateinit var adapter: ViewPagerFragmentAdapter
}

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var fab: FloatingActionButton
    private lateinit var pendingTaskAdapter: PendingTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main))
        { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        toolbar = findViewById(R.id.task_toolbar)
        fab = findViewById(R.id.fab)

        val viewPager: ViewPager = findViewById(R.id.viewPager)
        val tabLayout: TabLayout = findViewById(R.id.tab_layout)

        // Add fragments and titles, passing the adapter
        val fragments = listOf(
            pendingFragment(),
            completedFragment(),
            overdueFragment()
        )
        val titles = listOf("Pending", "Completed", "Overdue")

        // Initialize the adapter AFTER creating fragments and titles
        AppManager.adapter = ViewPagerFragmentAdapter(supportFragmentManager, fragments, titles)

        viewPager.adapter = AppManager.adapter
        tabLayout.setupWithViewPager(viewPager)

        pendingTaskAdapter = PendingTaskAdapter(TaskManager.pendingTasks,AppManager.adapter)

        fab.setOnClickListener {
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.add_task_action)

            val editTaskName = dialog.findViewById<android.widget.EditText>(R.id.edit_task_name)
            val editTaskDescriptionName = dialog.findViewById<android.widget.EditText>(R.id.edit_task_description_name)
            val dueDate = dialog.findViewById<android.widget.TextView>(R.id.due_date)
            val saveTask = dialog.findViewById<FloatingActionButton>(R.id.save_task)

            saveTask.setOnClickListener {
                val taskTitle = editTaskName.text.toString()
                if (taskTitle.isNotEmpty()) {
                    val taskDescription = editTaskDescriptionName.text.toString()
                    val taskDueDate = dueDate.text.toString()

                    val newTask = AddTaskModel(taskTitle, taskDescription, taskDueDate)
                    TaskManager.pendingTasks.add(newTask)

                    // Update the pending fragment's RecyclerView
                    AppManager.adapter.fragments[0].view?.findViewById<RecyclerView>(R.id.pending_task_recycler_view)?.adapter?.notifyItemInserted(TaskManager.pendingTasks.size - 1)
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                } else {
                    editTaskName.error = "Please enter a task title"
                    Toast.makeText(this, "Please enter a task title", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.show()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.title = "All Lists"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.feedback -> {
                Toast.makeText(this, "Feedback", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.about -> {
                Toast.makeText(this, "About", Toast.LENGTH_SHORT).show()
                true
            }
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}