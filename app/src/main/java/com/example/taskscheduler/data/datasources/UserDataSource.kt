package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    fun getUser(userId: String): Flow<UserEntity>

    suspend fun addUser(userEntity: UserEntity)
}