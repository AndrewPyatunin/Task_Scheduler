package com.example.taskscheduler.domain.usecases

import android.net.Uri
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.UserRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class UpdateUserDataUseCase @Inject constructor(
    private val repository: UserRepository
) {

    suspend fun execute(uri: Uri?, name: String, user: User, scope: CoroutineScope) = repository.update(uri, name, user, scope)
}