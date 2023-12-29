package com.example.taskscheduler.domain.models

import android.os.Parcelable
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("creatorId")
    val creatorId: String = "",
    @SerializedName("members")
    val members: List<String> = emptyList(),
    @SerializedName("description")
    val description: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("listOfTasks")
    val listOfTasks: List<CheckNoteItem> = emptyList(),
    @SerializedName("priority")
    val priority: UrgencyOfNote = UrgencyOfNote.LOW
) : Parcelable