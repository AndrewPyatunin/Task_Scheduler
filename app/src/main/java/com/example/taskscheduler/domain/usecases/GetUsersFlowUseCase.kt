package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository
import javax.inject.Inject

class GetUsersFlowUseCase @Inject constructor(
    private val repository: UserRepository
) {

    fun execute() = repository.getUsers()
}