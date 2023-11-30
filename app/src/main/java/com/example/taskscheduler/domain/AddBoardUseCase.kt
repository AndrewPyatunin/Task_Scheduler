package com.example.taskscheduler.domain

class AddBoardUseCase(
    private val repository: TaskRepository
) {

    fun execute(name: String, user: User, urlBackground: String, board: Board) {
        repository.createNewBoard(name, user, urlBackground, board)
    }
}