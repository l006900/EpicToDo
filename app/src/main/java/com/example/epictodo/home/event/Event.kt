package com.example.epictodo.home.event

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val date: Long,
    val startTime: Long,
    val endTime: Long,
    val frequency: String,
    val dailyCheckIns: Int,
    val checkInRecords: String,
    val reminder: String,
    val journalEntry: String
)