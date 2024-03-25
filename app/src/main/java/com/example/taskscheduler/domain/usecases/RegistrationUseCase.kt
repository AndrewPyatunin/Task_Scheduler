package com.example.taskscheduler.domain.usecases

import android.net.Uri
import com.example.taskscheduler.domain.repos.UserAuth
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class RegistrationUseCase @Inject constructor(
    private val userAuth: UserAuth
) {
    suspend fun execute(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): User {
        return userAuth.signUp(email, password, name, lastName, uri, scope)
    }
}