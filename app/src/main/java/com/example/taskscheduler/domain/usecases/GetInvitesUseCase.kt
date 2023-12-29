package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.InviteRepository

class GetInvitesUseCase(
    private val repository: InviteRepository
) {

    fun execute() = repository.getInvitesFromRoom()
}