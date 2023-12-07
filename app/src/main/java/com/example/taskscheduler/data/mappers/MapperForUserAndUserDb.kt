package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.domain.models.User

class MapperForUserAndUserDb {

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

    fun mapList(list: List<UserEntity>): List<User> {
        return list.map { map(it) }
    }

    fun mapToDb(user: User): UserEntity {
        return UserEntity(
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

    fun mapListToDb(list: List<User>): List<UserEntity> {
        return list.map { mapToDb(it) }
    }
}