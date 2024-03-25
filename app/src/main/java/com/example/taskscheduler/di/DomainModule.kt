package com.example.taskscheduler.di

import com.example.taskscheduler.data.repos.*
import com.example.taskscheduler.domain.repos.UserAuth
import com.example.taskscheduler.domain.repos.*
import dagger.Binds
import dagger.Module

@Module
interface DomainModule {

    @Binds
    fun bindBoardRepository(impl: BoardRepositoryImpl): BoardRepository

    @Binds
    fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindUserAuth(impl: UserAuthentication): UserAuth

    @Binds
    fun bindNotesListRepository(impl: NotesListRepositoryImpl): NotesListRepository

    @Binds
    fun bindNoteRepository(impl: NoteRepositoryImpl): NoteRepository

    @Binds
    fun bindInviteRepository(impl: InviteRepositoryImpl): InviteRepository
}