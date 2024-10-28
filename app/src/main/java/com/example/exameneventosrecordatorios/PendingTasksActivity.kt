package com.example.exameneventosrecordatorios

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PendingTasksActivity : AppCompatActivity() {

    private lateinit var listViewPendingTasks: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val pendingTasksList = mutableListOf<String>()

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_tasks)

        listViewPendingTasks = findViewById(R.id.listViewPendingTasks)
        dbHelper = DatabaseHelper(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, pendingTasksList)
        listViewPendingTasks.adapter = adapter

        loadPendingTasksFromDatabase()
    }

    private fun loadPendingTasksFromDatabase() {
        val tasks = dbHelper.getTasks(isDone = false)
        pendingTasksList.clear()
        pendingTasksList.addAll(tasks.map { it.description })
        adapter.notifyDataSetChanged()
    }
}
