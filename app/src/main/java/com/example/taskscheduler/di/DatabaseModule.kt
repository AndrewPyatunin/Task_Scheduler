package com.example.taskscheduler.di

import android.app.Application
import com.example.taskscheduler.data.database.*
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    fun provideDatabase(context: Application): TaskDatabase {
        return TaskDatabase.getInstance(context)
    }

    @Provides
    fun provideUserDao(database: TaskDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideBoardDao(database: TaskDatabase): BoardDao {
        return database.boardDao()
    }

    @Provides
    fun provideNotesListDao(database: TaskDatabase): NotesListDao {
        return database.notesListDao()
    }

    @Provides
    fun provideNoteDao(database: TaskDatabase): NoteDao {
        return database.noteDao()
    }

    @Provides
    fun provideInviteDao(database: TaskDatabase): InviteDao {
        return database.inviteDao()
    }
}