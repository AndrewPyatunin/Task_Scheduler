package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.InviteRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class FetchInvitesUseCase @Inject constructor(
    private val repository: InviteRepository
) {

    suspend fun execute(scope: CoroutineScope) = repository.getInvites(scope)
}