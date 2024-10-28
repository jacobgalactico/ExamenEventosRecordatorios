package com.example.exameneventosrecordatorios

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DoneTasksActivity : AppCompatActivity() {

    private lateinit var listViewDoneTasks: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val doneTasksList = mutableListOf<String>()

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_done_tasks)

        listViewDoneTasks = findViewById(R.id.listViewDoneTasks)
        dbHelper = DatabaseHelper(this)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, doneTasksList)
        listViewDoneTasks.adapter = adapter

        loadDoneTasksFromDatabase()
    }

    private fun loadDoneTasksFromDatabase() {
        val tasks = dbHelper.getTasks(isDone = true)
        doneTasksList.clear()
        doneTasksList.addAll(tasks.map { it.description })
        adapter.notifyDataSetChanged()
    }
}
