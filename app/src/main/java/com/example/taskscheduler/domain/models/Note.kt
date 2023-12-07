package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    @PrimaryKey
    val id: String = "",
    val title: String = "",
    val creatorId: String = "",
    val members: List<User> = emptyList(),
    val description: String = "",
    val date: String = "",
    val listOfTasks: List<CheckNoteItem> = ArrayList(),
    val priority: UrgencyOfNote = UrgencyOfNote.LOW
    ) :
    Parcelable