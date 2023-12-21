package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.data.entities.UserForInvitesEntity
import kotlinx.coroutines.flow.Flow

interface InviteDataSource {

    fun getInvitesFlow(): Flow<List<InviteEntity>>

    fun getUsersForInvites(): Flow<List<UserForInvitesEntity>>

    suspend fun addInvite(inviteEntity: InviteEntity)

    suspend fun removeInvite(inviteEntity: InviteEntity)

    suspend fun addUserForInvites(userForInvitesEntity: UserForInvitesEntity)
}