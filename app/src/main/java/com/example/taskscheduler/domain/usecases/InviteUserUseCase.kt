package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.repos.InviteRepository
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class InviteUserUseCase @Inject constructor(
    private val repository: InviteRepository
) {
    suspend fun execute(userForInvite: User, currentUser: User, board: Board, scope: CoroutineScope) {
        return repository.inviteUser(userForInvite, currentUser, board, scope)
    }
}