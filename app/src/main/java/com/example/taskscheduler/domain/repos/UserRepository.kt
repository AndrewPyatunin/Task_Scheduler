package com.example.taskscheduler.domain.repos

import android.net.Uri
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun addUser(user: User)

    fun getUser(userId: String): Flow<User>

    suspend fun updateUserProfile(description: String, email: String)

    suspend fun update(uri: Uri?, name: String)

    suspend fun updateUserEmail(email: String, ref: DatabaseReference): String

    fun updateStatus()
}