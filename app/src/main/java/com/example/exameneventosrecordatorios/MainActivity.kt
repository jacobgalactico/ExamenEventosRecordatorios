package com.example.exameneventosrecordatorios

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.ComponentActivity
import java.util.UUID

class MainActivity : ComponentActivity() {

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var buttonShowPending: Button
    private lateinit var buttonShowDone: Button
    private lateinit var listViewTasks: ListView

    private var showingPending = true
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var taskAdapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTask = findViewById(R.id.editTextTask)
        buttonAddTask = findViewById(R.id.buttonAddTask)
        buttonShowPending = findViewById(R.id.buttonShowPending)
        buttonShowDone = findViewById(R.id.buttonShowDone)
        listViewTasks = findViewById(R.id.listViewTasks)

        dbHelper = DatabaseHelper(this)

        // Initialize the custom adapter with the delete function
        taskAdapter = TaskAdapter(this, taskList, dbHelper) { task ->
            // Logic to mark the task as completed
            markTaskAsDone(task)
        }

        listViewTasks.adapter = taskAdapter

        buttonAddTask.setOnClickListener {
            val taskDescription = editTextTask.text.toString()
            if (taskDescription.isNotEmpty()) {
                val task = Task(id = UUID.randomUUID().toString(), description = taskDescription, isDone = false)
                // Añadir log aquí para verificar el ID de la tarea
                Log.d("MainActivity", "Creating new task with ID: ${task.id}")

                dbHelper.addTask(task)
                editTextTask.text.clear()
                Toast.makeText(this, "Task added: $taskDescription", Toast.LENGTH_SHORT).show()
                loadTasksFromDatabase()
            } else {
                Toast.makeText(this, "The task cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }


        buttonShowPending.setOnClickListener {
            showingPending = true
            loadTasksFromDatabase()
        }

        buttonShowDone.setOnClickListener {
            showingPending = false
            loadTasksFromDatabase()
        }

        // Load tasks initially
        loadTasksFromDatabase()
    }

    private fun loadTasksFromDatabase() {
        // Load pending or completed tasks based on `showingPending`
        taskList.clear()
        taskList.addAll(dbHelper.getTasks(isDone = !showingPending))
        taskAdapter.notifyDataSetChanged()
    }

    private fun markTaskAsDone(task: Task) {
        // Update the task status to completed in the database
        task.id?.let { dbHelper.markTaskAsDone(it) }

        // Reload the list to reflect changes in the UI
        loadTasksFromDatabase()
    }
}