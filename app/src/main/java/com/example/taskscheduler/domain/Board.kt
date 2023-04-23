package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Board(val name: String, val members: ArrayList<User>, val listsOfNotes: List<Note>, val notes: ArrayList<Note>) :
    Parcelable {
}