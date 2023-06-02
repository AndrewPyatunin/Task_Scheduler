package com.example.taskscheduler.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BackgroundImage(val id: String, val imageUrl: String, var isChosen: Boolean): Parcelable{
}