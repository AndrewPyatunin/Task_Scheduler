package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfNotesItem(
    var id: String = "",
    val title: String = "",
    val creatorId: String = "",
    var listNotes: Map<String, Boolean> = HashMap()
    ) : Parcelable