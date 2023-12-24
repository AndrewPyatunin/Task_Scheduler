package com.example.taskscheduler.domain

import android.net.Uri
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserAuth {

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): User

    fun updateUserAvatar(uri: Uri, name: String)

    suspend fun uploadUserAvatar(
        uri: Uri,
        name: String
    ): Result<String>
}