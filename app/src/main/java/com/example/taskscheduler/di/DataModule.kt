package com.example.taskscheduler.di

import com.example.taskscheduler.data.datasources.*
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @Binds
    fun bindBoardDataSource(impl: BoardDataSourceImpl): BoardDataSource

    @Binds
    fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource

    @Binds
    fun bindNotesListDataSource(impl: NotesListDataSourceImpl): NotesListDataSource

    @Binds
    fun bindNoteDataSource(impl: NoteDataSourceImpl): NoteDataSource

    @Binds
    fun bindInviteDataSource(impl: InviteDataSourceImpl): InviteDataSource
}