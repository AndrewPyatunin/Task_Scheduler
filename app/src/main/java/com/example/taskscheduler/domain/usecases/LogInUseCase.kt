package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.UserAuth
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope

class LogInUseCase(
    private val repository: UserAuth
) {

    suspend fun execute(email: String, password: String, auth: FirebaseAuth, scope: CoroutineScope) {
        return repository.login(email, password, auth, scope)
    }
}