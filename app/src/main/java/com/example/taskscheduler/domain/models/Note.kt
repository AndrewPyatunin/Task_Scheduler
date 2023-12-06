package com.example.taskscheduler.domain.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey
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