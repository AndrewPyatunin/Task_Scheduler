package com.example.taskscheduler.presentation.inviteuser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.domain.Board

class InviteUserViewModelFactory(val board: Board) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InviteUserViewModel(board) as T
    }
}