package com.example.epictodo.group.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskMemberDao {
    @Query("SELECT * FROM task_members WHERE taskId = :taskId")
    fun getMembersForTask(taskId: Int): Flow<List<TaskMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskMember(taskMember: TaskMember)

    @Delete
    suspend fun deleteTaskMember(taskMember: TaskMember)
}

