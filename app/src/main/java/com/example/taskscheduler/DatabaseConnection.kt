package com.example.taskscheduler

import com.example.taskscheduler.domain.BackgroundImage
import com.example.taskscheduler.domain.NewCallback
import com.google.firebase.auth.FirebaseUser

interface DatabaseConnection {

    fun query(user: FirebaseUser)

    fun getBoard()

    fun getBackgroundImages(): List<BackgroundImage>

    fun getBackgroundImages(callback: NewCallback)
}