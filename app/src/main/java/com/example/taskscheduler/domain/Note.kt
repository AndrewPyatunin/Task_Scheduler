package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    var title: String = "",
    val members: List<User> = emptyList(),
    var description: String = "",
    val labels: String = "",
    val listOfTasks: List<CheckNoteItem> = ArrayList()
    ) :
    Parcelable