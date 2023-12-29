package com.example.taskscheduler.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotesItem(var listNotes: List<Note> = emptyList()) : Parcelable