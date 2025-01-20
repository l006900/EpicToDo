package com.example.epictodo.group.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members")
    fun getAllMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE memberId = :memberId")
    fun getMemberById(memberId: Int): Flow<Member>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Delete
    suspend fun deleteMember(member: Member)
}

