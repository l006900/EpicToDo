package com.example.epictodo.group.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Project::class,
            parentColumns = ["projectId"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val taskId: Int = 0,
    val projectId: Int,
    val title: String,
    val description: String,
    val dueDate: String,
    val priority: String,
    val status: String,
    val assignedTo: String
)

