package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository

class UpdateUserProfileUseCase(
    private val repository: UserRepository
) {

    suspend fun execute(description: String, email: String, user: User) {
        repository.updateUserProfile(description, email, user)
    }

}