package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.InviteEntity
import kotlinx.coroutines.flow.Flow

interface InviteDataSource {

    fun getInvitesFlow(): Flow<List<InviteEntity>>

    suspend fun addInvite(inviteEntity: InviteEntity)

    suspend fun addInvites(inviteList: List<InviteEntity>)

    suspend fun removeInvite(inviteEntity: InviteEntity)

    suspend fun clearAllInvites()
}