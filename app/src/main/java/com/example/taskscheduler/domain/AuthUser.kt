package com.example.taskscheduler.domain

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface AuthUser {

    fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): Flow<User>
}