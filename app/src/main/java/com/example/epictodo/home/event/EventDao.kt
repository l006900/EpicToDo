package com.example.epictodo.home.event

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {
    @Query("SELECT * FROM events")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE date BETWEEN :startDate AND :endDate")
    fun getEventsBetweenDates(startDate: Long, endDate: Long): LiveData<List<Event>>

    @Query("SELECT * FROM events WHERE startTime <= :endDate AND endTime >= :startDate")
    fun getAllEventsBetweenDates(startDate: Long, endDate: Long): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)
}