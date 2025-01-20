package com.example.epictodo.group.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FileDao {
    @Query("SELECT * FROM files WHERE taskId = :taskId")
    fun getFilesForTask(taskId: Int): Flow<List<File>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: File): Long

    @Update
    suspend fun updateFile(file: File)

    @Delete
    suspend fun deleteFile(file: File)
}

