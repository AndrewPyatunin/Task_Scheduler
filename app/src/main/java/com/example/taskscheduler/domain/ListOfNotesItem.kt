package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfNotesItem(
    val id: String = "",
    val title: String = "",
    val listNotes: Map<String, Note> = HashMap()//ArrayList<Note> = ArrayList()
    ) : Parcelable {
}