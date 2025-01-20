package com.example.epictodo.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "projects")
data class Project(
    @PrimaryKey(autoGenerate = true) val projectId: Int = 0,
    val name: String,
    val description: String,
    val status: ProjectStatus,
    val startDate: Date,
    val dueDate: Date,
    val progress: Int, // 0-100
    val ownerId: Int
)

enum class ProjectStatus {
    NOT_STARTED,
    IN_PROGRESS,
    ON_HOLD,
    COMPLETED
}

