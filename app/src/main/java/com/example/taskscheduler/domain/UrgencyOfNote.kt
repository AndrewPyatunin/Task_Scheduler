package com.example.taskscheduler.domain

enum class UrgencyOfNote(var prior: Int) {
    HIGH(2), MIDDLE(1), LOW(0)
}