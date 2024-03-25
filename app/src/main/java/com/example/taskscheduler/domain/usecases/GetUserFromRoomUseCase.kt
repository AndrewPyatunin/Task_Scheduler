package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserRepository
import javax.inject.Inject

class GetUserFromRoomUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend fun execute(userId: String) = repository.getUser(userId)
}