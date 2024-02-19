package com.example.taskscheduler.data.datasources

import com.example.taskscheduler.data.database.InviteDao
import com.example.taskscheduler.data.entities.InviteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InviteDataSourceImpl @Inject constructor(
    private val taskDatabaseDao: InviteDao
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