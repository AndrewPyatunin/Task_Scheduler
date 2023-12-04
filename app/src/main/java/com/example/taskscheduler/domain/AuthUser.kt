package com.example.taskscheduler.domain

import android.net.Uri
import com.example.taskscheduler.data.UserAuthState
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface AuthUser {

    fun signUp(
        email: String,
        password: String,
        name: String,
        lastName: String,
        uri: Uri?,
        scope: CoroutineScope
    ): Flow<UserAuthState>
}