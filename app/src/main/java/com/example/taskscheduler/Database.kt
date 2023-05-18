package com.example.taskscheduler

import com.google.firebase.auth.FirebaseUser

class Database {
    companion object {
        var connection: DatabaseConnection? = null
        fun getDbConnection(): DatabaseConnection {
            if (connection == null) {
                connection = MyDatabaseConnection()
            }
            return connection as DatabaseConnection
        }
    }
}