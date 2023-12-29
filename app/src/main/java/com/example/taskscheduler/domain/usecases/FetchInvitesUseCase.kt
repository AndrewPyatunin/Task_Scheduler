package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.InviteRepository
import kotlinx.coroutines.CoroutineScope

class FetchInvitesUseCase(
    private val repository: InviteRepository
) {

    suspend fun execute(scope: CoroutineScope) = repository.getInvites(scope)
}