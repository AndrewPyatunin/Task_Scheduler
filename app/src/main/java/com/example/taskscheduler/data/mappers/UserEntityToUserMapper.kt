package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.UserEntity
import com.example.taskscheduler.domain.models.User
import javax.inject.Inject

class UserEntityToUserMapper @Inject constructor() : Mapper<UserEntity?, User> {

    override fun map(from: UserEntity?): User {
        return User(
            id = from?.id ?: "",
            name = from?.name ?: "",
            lastName = from?.lastName ?: "",
            email = from?.email ?: "",
            onlineStatus = from?.onlineStatus ?: false,
            boards = from?.boards ?: emptyMap(),
            uri = from?.uri ?: "",
            description = from?.description ?: "",
            invites = from?.invites ?: emptyMap()
        )
    }
}