package com.example.epictodo.group.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "files",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class File(
    @PrimaryKey(autoGenerate = true) val fileId: Int = 0,
    val taskId: Int,
    val fileName: String,
    val fileType: String,
    val filePath: String
)

