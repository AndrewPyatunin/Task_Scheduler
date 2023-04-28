package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val name: String,
    val members: List<User>,
    val description: String,
    val labels: String,
    val listOfTasks: List<String>
    ) :
    Parcelable