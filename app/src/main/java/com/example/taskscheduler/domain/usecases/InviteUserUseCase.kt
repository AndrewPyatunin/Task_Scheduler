package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.InviteRepository

class InviteUserUseCase(
    private val repository: InviteRepository
) {
    suspend fun execute(userForInvite: User, currentUser: User, board: Board): String {
        return repository.inviteUser(userForInvite, currentUser, board)
    }
}