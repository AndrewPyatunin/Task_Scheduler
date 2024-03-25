package com.example.taskscheduler.di

import com.example.taskscheduler.data.datasources.*
import dagger.Binds
import dagger.Module

@Module
interface DataModule {

    @AppScope
    @Binds
    fun bindBoardDataSource(impl: BoardDataSourceImpl): BoardDataSource

    @AppScope
    @Binds
    fun bindUserDataSource(impl: UserDataSourceImpl): UserDataSource

    @AppScope
    @Binds
    fun bindNotesListDataSource(impl: NotesListDataSourceImpl): NotesListDataSource

    @AppScope
    @Binds
    fun bindNoteDataSource(impl: NoteDataSourceImpl): NoteDataSource

    @AppScope
    @Binds
    fun bindInviteDataSource(impl: InviteDataSourceImpl): InviteDataSource
}