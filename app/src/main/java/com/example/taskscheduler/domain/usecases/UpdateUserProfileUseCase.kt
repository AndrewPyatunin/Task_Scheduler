package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(
    private val repository: UserRepository
) {

    suspend fun execute(description: String, email: String, user: User, scope: CoroutineScope) {
        return repository.updateUserProfile(description, email, user, scope)
    }
}