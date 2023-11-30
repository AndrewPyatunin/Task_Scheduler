package com.example.taskscheduler.domain

data class NewBoardArguments(
    val name: String,
    val user: User,
    val urlBackground: String,
    val board: Board
)
