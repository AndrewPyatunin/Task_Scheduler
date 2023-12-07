package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.domain.models.User

class UserForInvitesToUserMapper {

    fun map(userForInvitesEntity: UserForInvitesEntity): User {
        return User(
            id = userForInvitesEntity.id,
            name = userForInvitesEntity.name,
            lastName = userForInvitesEntity.lastName,
            email = userForInvitesEntity.email,
            onlineStatus = userForInvitesEntity.onlineStatus,
            boards = userForInvitesEntity.boards,
            uri = userForInvitesEntity.uri,
            description = userForInvitesEntity.description,
            invites =  userForInvitesEntity.invites
        )
    }
}