package com.example.taskscheduler

import com.google.firebase.auth.FirebaseUser

interface DatabaseConnection {
    fun query(user: FirebaseUser)
}