package com.example.taskscheduler.domain

class AddUserUseCase(
    private val repository: TaskRepository
) {

    fun execute(user: User) {
        repository.addUser(user)
    }
}