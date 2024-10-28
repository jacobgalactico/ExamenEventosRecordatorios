package com.example.exameneventosrecordatorios

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TaskAdapter(
    context: Context,
    private val tasks: MutableList<Task>,
    private val dbHelper: DatabaseHelper,
    private val onDeleteClick: (Task) -> Unit
) : ArrayAdapter<Task>(context, 0, tasks) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item_task, parent, false)

        val task = getItem(position)
        val taskDescription = view.findViewById<TextView>(R.id.taskDescription)
        val buttonDeleteTask = view.findViewById<Button>(R.id.buttonDeleteTask)

        taskDescription.text = task?.description

        // Acción del botón de eliminar
        buttonDeleteTask.setOnClickListener {
            task?.let {
                Log.d("TaskAdapter", "Attempting to mark task as done with ID: ${it.id}")
                dbHelper.markTaskAsDone(it.id)
                tasks.remove(it)
                notifyDataSetChanged()
                onDeleteClick(it)
            }
        }

        return view
    }
}