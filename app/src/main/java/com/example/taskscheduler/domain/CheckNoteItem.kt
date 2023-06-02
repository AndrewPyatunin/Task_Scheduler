package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CheckNoteItem(
    val itemTitle: String = "",
    var isChecked: Boolean = false,
    var id: String = ""
): Parcelable