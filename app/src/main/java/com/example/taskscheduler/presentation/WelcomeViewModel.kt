package com.example.taskscheduler.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeViewModel: ViewModel() {
    private val _launchReady = MutableLiveData<String>()
    val launchReady: LiveData<String>
        get() = _launchReady

    init {
        val time = System.currentTimeMillis()
        var i = 0
        while (System.currentTimeMillis()-time < 2000) {
            i++
        }
        launchPrepare()
    }

    private fun launchPrepare() {
        _launchReady.value = "Ready"
    }
}