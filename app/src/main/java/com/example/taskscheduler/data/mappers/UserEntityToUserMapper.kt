package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.domain.models.User

class UserEntityToUserMapper {

    fun map(userEntity: UserEntity): User {
        return User(
            id = userEntity.id,
            name = userEntity.name,
            lastName = userEntity.lastName,
            email = userEntity.email,
            onlineStatus = userEntity.onlineStatus,
            boards = userEntity.boards,
            uri = userEntity.uri,
            description = userEntity.description,
            invites = userEntity.invites
        )
    }
}