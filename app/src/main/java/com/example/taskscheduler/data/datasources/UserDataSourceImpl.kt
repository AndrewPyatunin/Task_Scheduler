package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow

class UserDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : UserDataSource {

    override fun getUsers(): Flow<List<UserEntity>> {
        return taskDatabaseDao.getUsersFlow()
    }

    override suspend fun getUser(userId: String): UserEntity {
        return taskDatabaseDao.getUser(userId)
    }

    override suspend fun addUser(userEntity: UserEntity) {
        taskDatabaseDao.addUser(userEntity)
    }

    override suspend fun addAllUsers(users: List<UserEntity>) {
        taskDatabaseDao.addAllUsers(users)
    }

    override suspend fun clearAllUsersInRoom() {
        taskDatabaseDao.clearAllUsers()
    }
}