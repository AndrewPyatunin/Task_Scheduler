package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.modelsDb.UserForInvitesDb
import com.example.taskscheduler.domain.models.User

class MapperForUserAndUserForInvitesDb {

    fun map(userForInvitesDb: UserForInvitesDb): User {
        return User(
            id = userForInvitesDb.id,
            name = userForInvitesDb.name,
            lastName = userForInvitesDb.lastName,
            email = userForInvitesDb.email,
            onlineStatus = userForInvitesDb.onlineStatus,
            boards = userForInvitesDb.boards,
            uri = userForInvitesDb.uri,
            description = userForInvitesDb.description,
            invites =  userForInvitesDb.invites
        )
    }

    fun mapList(list: List<UserForInvitesDb>): List<User> {
        return list.map { map(it) }
    }

    fun mapToUserInvitesDb(user: User): UserForInvitesDb {
        return UserForInvitesDb(
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