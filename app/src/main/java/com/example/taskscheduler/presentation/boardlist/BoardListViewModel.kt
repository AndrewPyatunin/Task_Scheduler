package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.ViewModel
import com.example.taskscheduler.MyDatabaseConnection
import com.example.taskscheduler.domain.models.User
import com.example.taskscheduler.domain.usecases.GetBoardsFlowUseCase
import com.example.taskscheduler.domain.usecases.GetUserFlowFromRoomUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

class BoardListViewModel @Inject constructor(
    private val getBoardsFlowUseCase: GetBoardsFlowUseCase,
    private val getUserFlowFromRoomUseCase: GetUserFlowFromRoomUseCase,
    private val auth: FirebaseAuth
) : ViewModel() {

    fun getBoardsFlow(user: User) = getBoardsFlowUseCase.execute(user).onEach {

    }.onStart { }


    val userFlow = (auth.currentUser?.uid ?: MyDatabaseConnection.userId)?.let {
        getUserFlowFromRoomUseCase.execute(
            it
        )
    }

    fun logout() {
        auth.signOut()
    }
}