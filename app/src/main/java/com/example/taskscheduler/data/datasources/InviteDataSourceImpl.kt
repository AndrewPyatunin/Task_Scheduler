package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.TaskDatabaseDao
import com.example.taskscheduler.data.entities.InviteEntity
import kotlinx.coroutines.flow.Flow

class InviteDataSourceImpl(
    private val taskDatabaseDao: TaskDatabaseDao
) : InviteDataSource {

    override fun getInvitesFlow(): Flow<List<InviteEntity>> {
        return taskDatabaseDao.getInvitesFlow()
    }

    override suspend fun addInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.addInvite(inviteEntity)
    }

    override suspend fun addInvites(inviteList: List<InviteEntity>) {
        taskDatabaseDao.addInvites(inviteList)
    }

    override suspend fun removeInvite(inviteEntity: InviteEntity) {
        taskDatabaseDao.removeInvite(inviteEntity.id)
    }

    override suspend fun clearAllInvites() {
        taskDatabaseDao.clearAllInvites()
    }
}