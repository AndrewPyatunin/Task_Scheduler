package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(
    val id: String = "",
    var title: String = "",
    val creatorId: String = "",
    val backgroundUrl: String = "",
    val members: List<String> = emptyList(),
    var listsOfNotesIds: List<String> = emptyList()

    ) :
    Parcelable