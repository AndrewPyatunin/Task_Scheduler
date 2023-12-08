package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.domain.models.User

class UserToUserForInvitesMapper : Mapper<User, UserForInvitesEntity> {

    override fun map(from: User): UserForInvitesEntity {
        return UserForInvitesEntity(
            id = from.id,
            name = from.name,
            lastName = from.lastName,
            email = from.email,
            onlineStatus = from.onlineStatus,
            boards = from.boards,
            uri = from.uri,
            description = from.description,
            invites =  from.invites
        )
    }
}