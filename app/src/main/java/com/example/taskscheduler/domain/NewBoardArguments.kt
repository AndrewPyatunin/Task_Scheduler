package com.example.taskscheduler.domain

import com.example.taskscheduler.domain.models.Board
import com.example.taskscheduler.domain.models.User

data class NewBoardArguments(
    val name: String,
    val user: User,
    val urlBackground: String,
    val board: Board
)
