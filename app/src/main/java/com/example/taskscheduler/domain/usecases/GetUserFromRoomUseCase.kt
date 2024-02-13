package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository

class GetUserFromRoomUseCase(
    private val repository: UserRepository
) {
    suspend fun execute(userId: String) = repository.getUser(userId)
}