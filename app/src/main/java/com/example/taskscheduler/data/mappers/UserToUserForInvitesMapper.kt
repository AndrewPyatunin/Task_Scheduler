package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.domain.models.User

class UserToUserForInvitesMapper {

    fun map(user: User): UserForInvitesEntity {
        return UserForInvitesEntity(
            id = user.id,
            name = user.name,
            lastName = user.lastName,
            email = user.email,
            onlineStatus = user.onlineStatus,
            boards = user.boards,
            uri = user.uri,
            description = user.description,
            invites =  user.invites
        )
    }
}