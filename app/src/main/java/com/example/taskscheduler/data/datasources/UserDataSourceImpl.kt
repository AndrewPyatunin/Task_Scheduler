package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : UserDataSource {

    override fun getUser(userId: String): Flow<UserEntity> {
        return taskDatabaseDao.getUser(userId)
    }

    override suspend fun addUser(userEntity: UserEntity) {
        taskDatabaseDao.addUser(userEntity)
    }
}