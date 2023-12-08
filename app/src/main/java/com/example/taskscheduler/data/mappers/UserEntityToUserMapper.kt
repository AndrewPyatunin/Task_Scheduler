package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.domain.models.User

class UserEntityToUserMapper : Mapper<UserEntity, User> {

    override fun map(from: UserEntity): User {
        return User(
            id = from.id,
            name = from.name,
            lastName = from.lastName,
            email = from.email,
            onlineStatus = from.onlineStatus,
            boards = from.boards,
            uri = from.uri,
            description = from.description,
            invites = from.invites
        )
    }
}