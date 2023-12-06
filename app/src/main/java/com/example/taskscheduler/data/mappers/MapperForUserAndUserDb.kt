package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.modelsDb.UserDb
import com.example.taskscheduler.domain.models.User

class MapperForUserAndUserDb {

    fun map(userDb: UserDb): User {
        return User(
            id = userDb.id,
            name = userDb.name,
            lastName = userDb.lastName,
            email = userDb.email,
            onlineStatus = userDb.onlineStatus,
            boards = userDb.boards,
            uri = userDb.uri,
            description = userDb.description,
            invites = userDb.invites
        )
    }

    fun mapList(list: List<UserDb>): List<User> {
        return list.map { map(it) }
    }

    fun mapToDb(user: User): UserDb {
        return UserDb(
            id = user.id,
            name = user.name,
            lastName = user.lastName,
            email = user.email,
            onlineStatus = user.onlineStatus,
            boards = user.boards,
            uri = user.uri,
            description = user.description,
            invites = user.invites
        )
    }

    fun mapListToDb(list: List<User>): List<UserDb> {
        return list.map { mapToDb(it) }
    }
}