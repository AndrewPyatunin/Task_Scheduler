package com.example.taskscheduler.presentation.boardlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.taskscheduler.domain.User

class BoardListViewModelFactory(val user: User): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return BoardListViewModel(user) as T
    }
}