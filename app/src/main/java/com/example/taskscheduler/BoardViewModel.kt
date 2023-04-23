package com.example.taskscheduler

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BoardViewModel: ViewModel() {
    private val _liveData = MutableLiveData<BoardViewModel>()
    val livedata: LiveData<BoardViewModel> = _liveData
}