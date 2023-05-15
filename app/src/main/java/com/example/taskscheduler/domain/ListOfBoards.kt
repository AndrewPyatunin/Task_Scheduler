package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListOfBoards(val listBoards: ArrayList<Board>): Parcelable
