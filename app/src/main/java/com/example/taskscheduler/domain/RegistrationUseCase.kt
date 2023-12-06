package com.example.taskscheduler.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class RegistrationUseCase(
    private val authUser: AuthUser
) {
    fun execute(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): Flow<User> {
        return authUser.signUp(email, password, name, lastName, uri, scope)
    }
}