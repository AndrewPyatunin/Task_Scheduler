package com.example.taskscheduler.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.taskscheduler.data.entities.InviteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InviteDao {

    @Query("SELECT * FROM invites")
    fun getInvitesFlow(): Flow<List<InviteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvite(invite: InviteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addInvites(inviteList: List<InviteEntity>)

    @Query("DELETE FROM invites WHERE id = :id")
    suspend fun removeInvite(id: String)

    @Query("DELETE FROM invites")
    suspend fun clearAllInvites()
}