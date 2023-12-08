package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.domain.models.Invite

class InviteToInviteEntityMapper : Mapper<Invite, InviteEntity> {

    override fun map(from: Invite): InviteEntity {
        return InviteEntity(
            id = from.id,
            boardId = from.boardId,
            userSenderId = from.userSenderId,
            userName = from.userName,
            userLastName = from.userLastName,
            boardName = from.boardName
        )
    }
}