package com.example.exameneventosrecordatorios

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "tasks.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_TASKS = "tasks"
        private const val COLUMN_ID = "id"
        private const val COLUMN_DESCRIPTION = "description"
        private const val COLUMN_IS_DONE = "is_done"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
            CREATE TABLE $TABLE_TASKS (
                $COLUMN_ID TEXT PRIMARY KEY,
                $COLUMN_DESCRIPTION TEXT NOT NULL,
                $COLUMN_IS_DONE INTEGER NOT NULL
            )
        """
        db?.execSQL(createTableQuery)
        Log.d("DatabaseHelper", "Table $TABLE_TASKS created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TASKS")
        onCreate(db)
    }

    // Insertar tarea
    fun addTask(task: Task): String? {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put(COLUMN_ID, task.id)
                put(COLUMN_DESCRIPTION, task.description)
                put(COLUMN_IS_DONE, if (task.isDone) 1 else 0)
            }
            db.insert(TABLE_TASKS, null, values)
            Log.d("DatabaseHelper", "Task added with ID: ${task.id}")
            task.id
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding task", e)
            null
        } finally {
            db.close()
        }
    }

    // Obtener todas las tareas
    fun getTasks(isDone: Boolean): List<Task> {
        val db = readableDatabase
        val tasks = mutableListOf<Task>()
        val cursor = db.query(
            TABLE_TASKS,
            arrayOf(COLUMN_ID, COLUMN_DESCRIPTION, COLUMN_IS_DONE),
            "$COLUMN_IS_DONE = ?",
            arrayOf(if (isDone) "1" else "0"),
            null, null, null
        )
        with(cursor) {
            while (moveToNext()) {
                val id = getString(getColumnIndex(COLUMN_ID)) ?: ""
                val description = getString(getColumnIndex(COLUMN_DESCRIPTION)) ?: ""
                val isDoneValue = getInt(getColumnIndex(COLUMN_IS_DONE)) == 1

                if (id.isNotEmpty() && description.isNotEmpty()) {
                    val task = Task(id = id, description = description, isDone = isDoneValue)
                    tasks.add(task)
                } else {
                    Log.e("DatabaseHelper", "Error: Task data is null or incomplete")
                }
            }
        }
        cursor.close()
        db.close()
        return tasks
    }

    // Actualizar tarea a completada
    fun markTaskAsDone(id: String) {
        val db = writableDatabase
        try {
            val values = ContentValues().apply {
                put(COLUMN_IS_DONE, 1) // Actualiza el campo isDone a 1 (true)
            }
            val rowsUpdated = db.update(TABLE_TASKS, values, "$COLUMN_ID = ?", arrayOf(id))
            Log.d("DatabaseHelper", "Rows updated: $rowsUpdated for task ID: $id")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error marking task as done", e)
        } finally {
            db.close()
        }
    }

    // Eliminar tarea
    fun deleteTask(id: String) {
        val db = writableDatabase
        try {
            val rowsDeleted = db.delete(TABLE_TASKS, "$COLUMN_ID = ?", arrayOf(id))
            Log.d("DatabaseHelper", "Rows deleted: $rowsDeleted for task ID: $id")
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error deleting task", e)
        } finally {
            db.close()
        }
    }
}
