package com.example.taskscheduler.domain

import android.os.Parcelable
import com.example.taskscheduler.domain.models.Board
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfBoards(
    val listBoards: ArrayList<Board>,
    var id: String
    ): Parcelable
