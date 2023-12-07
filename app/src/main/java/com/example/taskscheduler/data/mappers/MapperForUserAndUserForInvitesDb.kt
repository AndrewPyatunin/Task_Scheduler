package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserForInvitesEntity
import com.example.taskscheduler.domain.models.User

class MapperForUserAndUserForInvitesDb {

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

    fun mapList(list: List<UserForInvitesEntity>): List<User> {
        return list.map { map(it) }
    }

    fun mapToUserInvitesDb(user: User): UserForInvitesEntity {
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