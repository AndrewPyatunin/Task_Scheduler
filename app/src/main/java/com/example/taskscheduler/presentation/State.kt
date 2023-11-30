package com.example.taskscheduler.presentation

sealed class State {
    object Loading: State()
    class Error(val message: String): State()
    data class Result<T>(val result: T): State()
}

