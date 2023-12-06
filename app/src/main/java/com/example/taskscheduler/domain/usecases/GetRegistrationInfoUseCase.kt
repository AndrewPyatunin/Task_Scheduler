package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.AuthUser
import kotlinx.coroutines.flow.Flow

class GetRegistrationInfoUseCase(
    private val authUser: AuthUser
) {
    fun execute(): Flow<String> {
        return authUser.flowRegistrToast()
    }
}