package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Invite
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.InviteRepository
import javax.inject.Inject

class DeclineInviteUseCase @Inject constructor(
    private val repository: InviteRepository
) {

    suspend fun execute(user: User, invite: Invite) = repository.clearInviteInDatabase(user, invite)
}