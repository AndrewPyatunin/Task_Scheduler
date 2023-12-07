package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.domain.models.Invite

class InviteToInviteEntityMapper {

    fun map(invite: Invite): InviteEntity {
        return InviteEntity(
            id = invite.id,
            boardId = invite.boardId,
            userSenderId = invite.userSenderId,
            userName = invite.userName,
            userLastName = invite.userLastName,
            boardName = invite.boardName
        )
    }
}