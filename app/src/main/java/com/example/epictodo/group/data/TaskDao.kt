package com.example.epictodo.group.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE projectId = :projectId")
    fun getTasksForProject(projectId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    fun getTaskById(taskId: Int): Flow<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}

