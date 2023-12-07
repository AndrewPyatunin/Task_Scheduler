package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.domain.models.Invite

class InviteEntityToInviteMapper {

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

}