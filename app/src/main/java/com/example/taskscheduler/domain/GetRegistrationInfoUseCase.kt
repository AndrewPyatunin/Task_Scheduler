package com.example.taskscheduler.domain

import kotlinx.coroutines.flow.Flow

class GetRegistrationInfoUseCase(
    private val authUser: AuthUser
) {
    fun execute(): Flow<String> {
        return authUser.flowRegistrToast()
    }
}