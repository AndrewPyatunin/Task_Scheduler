package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface InviteRepository {

    suspend fun getInvites(scope: CoroutineScope)

    suspend fun acceptInvite(user: User, invite: Invite)

    suspend fun declineInvite(user: User, invite: Invite)

    suspend fun clearInviteInDatabase(user: User, invite: Invite)

    suspend fun inviteUser(userForInvite: User, currentUser: User, board: Board): String

    suspend fun addInvite(invite: Invite)

    suspend fun addUserForInvites(user: User)

    fun getInvitesFromRoom(): Flow<List<Invite>>

    fun getUsersForInvites(): Flow<List<User>>
}