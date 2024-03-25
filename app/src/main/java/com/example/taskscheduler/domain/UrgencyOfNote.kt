package com.example.taskscheduler.domain

enum class UrgencyOfNote(val prior: Int) {
    HIGH(2), MIDDLE(1), LOW(0)
}