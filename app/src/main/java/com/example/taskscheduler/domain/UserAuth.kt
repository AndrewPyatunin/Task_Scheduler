package com.example.taskscheduler.domain

import android.net.Uri
import com.example.taskscheduler.domain.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserAuth {

    fun updateUserAvatar(uri: Uri, name: String)

    fun logout(user: User, scope: CoroutineScope)

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): User

    suspend fun uploadUserAvatar(
        uri: Uri,
        name: String
    ): Result<String>

    suspend fun getUser(userId: String, scope: CoroutineScope)

}