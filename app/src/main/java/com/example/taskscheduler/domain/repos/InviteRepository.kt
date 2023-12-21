package com.example.taskscheduler.domain.repos

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.flow.Flow

interface InviteRepository {

    fun getInvites(): Flow<List<Invite>>

    fun acceptInvite(user: User, invite: Invite)

    fun declineInvite(user: User, invite: Invite)

    fun clearInviteInDatabase(userId: String, inviteBoardId: String)

    suspend fun inviteUser(userForInvite: User, currentUser: User, board: Board): String

    suspend fun addInvite(invite: Invite)

    suspend fun addUserForInvites(user: User)
}