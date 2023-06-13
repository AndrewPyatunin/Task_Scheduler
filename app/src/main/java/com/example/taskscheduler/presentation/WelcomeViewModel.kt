package com.example.taskscheduler.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskscheduler.MyDatabaseConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeViewModel: ViewModel() {
    private val _launchReady = MutableLiveData<String>()
    val launchReady: LiveData<String>
        get() = _launchReady

    init {
        viewModelScope.launch(Dispatchers.IO) {
            MyDatabaseConnection.onCallbackReady()
        }
        launchPrepare()
    }

    private fun launchPrepare() {
        _launchReady.value = "Ready"
    }
}