package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val id: String = "",
    val name: String = "",
    val members: List<String> = emptyList(),
    val listsOfNotes: Map<String, List<Note>> = emptyMap()
    ) :
    Parcelable