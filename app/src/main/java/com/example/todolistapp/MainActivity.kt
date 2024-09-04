package com.example.todolistapp

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Singleton for Task Management
object TaskManager {
    val pendingTasks: MutableList<AddTaskModel> = mutableListOf()
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
    private val REQUEST_CODE_SPEECH_TITLE = 1
    private val REQUEST_CODE_SPEECH_DESCRIPTION = 2

    fun startRecordingTitle(view: View) {
        startSpeechRecognition(REQUEST_CODE_SPEECH_TITLE, "What is the task title?")
    }

    fun startRecordingDescription(view: View) {
        startSpeechRecognition(REQUEST_CODE_SPEECH_DESCRIPTION, "What is the task description?")
    }

    private fun startSpeechRecognition(requestCode: Int, prompt: String) {
        // Check if Speech Recognition is available
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
            }
            startActivityForResult(intent, requestCode)
        } else {
            // Handle the case where the Speech Recognition Service is unavailable
            Toast.makeText(this, "Speech recognition is not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MainActivity", "onActivityResult - requestCode: $requestCode, resultCode: $resultCode, data: $data")

        if (resultCode == RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_CODE_SPEECH_TITLE -> {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenTitle = result?.get(0) ?: ""
                    Log.d("MainActivity", "Spoken Title: $spokenTitle")
                    findViewById<EditText>(R.id.edit_task_name)?.setText(spokenTitle)
                }
                REQUEST_CODE_SPEECH_DESCRIPTION -> {
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    val spokenDescription = result?.get(0) ?: ""
                    Log.d("MainActivity", "Spoken Description: $spokenDescription")
                    findViewById<EditText>(R.id.edit_task_description_name)?.setText(spokenDescription)
                }
            }
        } else {
            Log.e("MainActivity", "onActivityResult - resultCode: $resultCode, data: $data")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
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

        pendingTaskAdapter = PendingTaskAdapter(TaskManager.pendingTasks, AppManager.adapter)

        fab.setOnClickListener {
            val dialog: Dialog = Dialog(this)
            dialog.setContentView(R.layout.add_task_action)

            val editTaskName = dialog.findViewById<EditText>(R.id.edit_task_name)
            val editTaskDescriptionName = dialog.findViewById<EditText>(R.id.edit_task_description_name)
            val dueDate = dialog.findViewById<TextView>(R.id.due_date)
            val saveTask = dialog.findViewById<FloatingActionButton>(R.id.save_task)
            val calendarIcon = dialog.findViewById<ImageView>(R.id.calendar_icon)
            val micTitle = dialog.findViewById<ImageView>(R.id.mic_icon) // Mic icon for title
            val micDescription = dialog.findViewById<ImageView>(R.id.description_mic_icon) // Mic icon for description

            Log.d("MainActivity", "Dialog Views: editTaskName=$editTaskName, editTaskDescriptionName=$editTaskDescriptionName, dueDate=$dueDate, saveTask=$saveTask, calendarIcon=$calendarIcon, micTitle=$micTitle, micDescription=$micDescription")

            // Set up the calendar listener
            calendarIcon.setOnClickListener {
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = DatePickerDialog(
                    this,
                    { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                        val selectedDate = Calendar.getInstance()
                        selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                        // Format the selected date
                        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val formattedDate = dateFormat.format(selectedDate.time)
                        dueDate.setText(formattedDate)
                    },
                    year,
                    month,
                    day
                )

                // Only allow future dates
                datePickerDialog.datePicker.minDate = calendar.timeInMillis

                datePickerDialog.show()
            }

            // Set up Mic listeners
            micTitle?.setOnClickListener {
                startRecordingTitle(it) // Use 'it' to pass the clicked view
            }
            micDescription?.setOnClickListener {
                startRecordingDescription(it)
            }

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