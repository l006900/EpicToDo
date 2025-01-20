package com.example.epictodo.group.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val memberId: Int = 0,
    val name: String,
    val email: String,
    val role: String
)

