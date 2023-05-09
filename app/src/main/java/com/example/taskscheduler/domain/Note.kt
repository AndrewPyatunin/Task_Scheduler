package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    val title: String = "",
    val members: List<User> = emptyList(),
    val description: String = "",
    val labels: String = "",
    val listOfTasks: List<String> = emptyList()
    ) :
    Parcelable