package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.domain.models.Invite

class MapperForInviteAndInviteDb {

    fun map(inviteEntity: InviteEntity): Invite {
        return Invite(
            id = inviteEntity.id,
            boardId = inviteEntity.boardId,
            userSenderId = inviteEntity.userSenderId,
            userName = inviteEntity.userName,
            userLastName = inviteEntity.userLastName,
            boardName = inviteEntity.boardName
        )
    }

    fun mapList(list: List<InviteEntity>): List<Invite> {
        return list.map { map(it) }
    }

    fun mapToDb(invite: Invite): InviteEntity {
        return InviteEntity(
            id = invite.id,
            boardId = invite.boardId,
            userSenderId = invite.userSenderId,
            userName = invite.userName,
            userLastName = invite.userLastName,
            boardName = invite.boardName
        )
    }

    fun mapToDbList(list: List<Invite>): List<InviteEntity> {
        return list.map { mapToDb(it) }
    }
}