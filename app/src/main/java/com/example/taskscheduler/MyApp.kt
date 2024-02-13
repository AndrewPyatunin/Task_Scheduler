package com.example.taskscheduler

import android.app.Application
import com.example.taskscheduler.data.database.*
import com.example.taskscheduler.data.repos.*

class MyApp : Application() {

    companion object {

        private lateinit var userDao: UserDao
        private lateinit var boardDao: BoardDao
        private lateinit var notesListDao: NotesListDao
        private lateinit var noteDao: NoteDao
        private lateinit var inviteDao: InviteDao
        private lateinit var dao: TaskDatabaseDao

        val boardRepository by lazy {
            BoardRepositoryImpl(userDao, boardDao, notesListDao, noteDao)
        }
        val noteRepository by lazy {
            NoteRepositoryImpl(noteDao, notesListDao)
        }
        val notesListRepository by lazy {
            NotesListRepositoryImpl(notesListDao, boardDao)
        }
        val userRepository by lazy {
            UserRepositoryImpl(userDao)
        }
        val userAuthentication by lazy {
            UserAuthentication(userDao)
        }
        val inviteRepository by lazy {
            InviteRepositoryImpl(inviteDao, userDao)
        }

        fun initialize (application: Application) {
            synchronized(this) {
                dao = TaskDatabase.getInstance(application).taskDatabaseDao()
                userDao = TaskDatabase.getInstance(application).userDao()
                boardDao = TaskDatabase.getInstance(application).boardDao()
                notesListDao = TaskDatabase.getInstance(application).notesListDao()
                noteDao = TaskDatabase.getInstance(application).noteDao()
                inviteDao = TaskDatabase.getInstance(application).inviteDao()
            }
        }

        fun clear (application: Application) {
            synchronized(this) {
                TaskDatabase.getInstance(application).clearAllTables()
            }
        }
    }
}