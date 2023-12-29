package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun getUsers(): Flow<List<UserEntity>>

    suspend fun getUser(userId: String): UserEntity

    suspend fun addUser(userEntity: UserEntity)

    suspend fun addAllUsers(users: List<UserEntity>)

    suspend fun clearAllUsersInRoom()
}