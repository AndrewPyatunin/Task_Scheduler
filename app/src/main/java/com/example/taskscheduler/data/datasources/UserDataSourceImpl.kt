package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.database.UserDao
import com.example.taskscheduler.data.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val taskDatabaseDao: UserDao
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