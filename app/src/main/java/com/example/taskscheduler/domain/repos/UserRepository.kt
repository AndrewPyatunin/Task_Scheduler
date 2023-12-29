package com.example.taskscheduler.domain.repos

import android.net.Uri
import com.example.taskscheduler.domain.models.User
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun addUser(user: User)

    suspend fun getUser(userId: String): User

    suspend fun updateUserProfile(description: String, email: String, user: User)

    suspend fun update(uri: Uri?, name: String, user: User)

    suspend fun updateUserEmail(email: String, ref: DatabaseReference): String

    fun updateStatus()

    fun getUsers(): Flow<List<User>>

    suspend fun addAllUsers(scope: CoroutineScope)

    suspend fun clearAllUsers()
}