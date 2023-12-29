package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.InviteEntity
import com.example.taskscheduler.domain.models.Invite

class InviteEntityToInviteMapper : Mapper<InviteEntity?, Invite> {

    override fun map(from: InviteEntity?): Invite {
        return Invite(
            id = from?.id ?: "",
            boardId = from?.boardId ?: "",
            userSenderId = from?.userSenderId ?: "",
            userName = from?.userName ?: "",
            userLastName = from?.userLastName ?: "",
            boardName = from?.boardName ?: ""
        )
    }

}