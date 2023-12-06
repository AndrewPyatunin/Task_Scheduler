package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.TaskRepository
import com.example.taskscheduler.domain.models.User

class AddBoardUseCase(
    private val repository: TaskRepository
) {

    fun execute(name: String, user: User, urlBackground: String, board: Board) {
        repository.createNewBoard(name, user, urlBackground, board)
    }
}