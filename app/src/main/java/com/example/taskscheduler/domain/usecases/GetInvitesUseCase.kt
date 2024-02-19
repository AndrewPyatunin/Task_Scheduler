package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.InviteRepository
import javax.inject.Inject

class GetInvitesUseCase @Inject constructor(
    private val repository: InviteRepository
) {

    fun execute() = repository.getInvitesFromRoom()
}