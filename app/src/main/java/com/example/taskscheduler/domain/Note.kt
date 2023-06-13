package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val id: String = "",
    var title: String = "",
    val creatorId: String = "",
    val members: List<User> = emptyList(),
    var description: String = "",
    var date: String = "",
    val listOfTasks: List<CheckNoteItem> = ArrayList(),
    var priority: UrgencyOfNote = UrgencyOfNote.LOW
    ) :
    Parcelable