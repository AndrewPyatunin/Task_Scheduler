package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.data.entities.UserForInvitesEntity
import kotlinx.coroutines.flow.Flow

class InviteDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : InviteDataSource {

    override fun getInvitesFlow(): Flow<List<InviteEntity>> {
        return taskDatabaseDao.getInvitesFlow()
    }

    override fun getUsersForInvites(): Flow<List<UserForInvitesEntity>> {
        return taskDatabaseDao.getUsersForInvites()
    }

    override suspend fun addInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.addInvite(inviteEntity)
    }

    override suspend fun removeInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.removeInvite(inviteEntity.id)
    }

    override suspend fun addUserForInvites(userForInvitesEntity: UserForInvitesEntity) {
        taskDatabaseDao.addUserForInvites(userForInvitesEntity)
    }
}