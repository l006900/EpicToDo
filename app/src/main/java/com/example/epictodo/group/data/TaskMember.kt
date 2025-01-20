package com.example.epictodo.group.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "task_members",
    primaryKeys = ["taskId", "memberId"],
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["taskId"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Member::class,
            parentColumns = ["memberId"],
            childColumns = ["memberId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TaskMember(
    val taskId: Int,
    val memberId: Int
)

