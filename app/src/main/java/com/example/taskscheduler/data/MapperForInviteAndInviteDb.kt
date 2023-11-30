package com.example.taskscheduler.data

import com.example.taskscheduler.domain.Invite

class MapperForInviteAndInviteDb {

    fun map(inviteDb: InviteDb): Invite {
        return Invite(
            id = inviteDb.id,
            boardId = inviteDb.boardId,
            userSenderId = inviteDb.userSenderId,
            userName = inviteDb.userName,
            userLastName = inviteDb.userLastName,
            boardName = inviteDb.boardName
        )
    }

    fun mapList(list: List<InviteDb>): List<Invite> {
        return list.map { map(it) }
    }

    fun mapToDb(invite: Invite): InviteDb {
        return InviteDb(
            id = invite.id,
            boardId = invite.boardId,
            userSenderId = invite.userSenderId,
            userName = invite.userName,
            userLastName = invite.userLastName,
            boardName = invite.boardName
        )
    }

    fun mapToDbList(list: List<Invite>): List<InviteDb> {
        return list.map { mapToDb(it) }
    }
}