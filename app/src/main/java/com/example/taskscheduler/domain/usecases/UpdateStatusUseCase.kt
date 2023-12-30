package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository

class UpdateStatusUseCase(
    private val repository: UserRepository
) {

    fun execute() = repository.updateStatus()
}