package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(val id: Long, val name: String, val email: String, val boards: ArrayList<Board>) :
    Parcelable {
    private val password: String = ""
}