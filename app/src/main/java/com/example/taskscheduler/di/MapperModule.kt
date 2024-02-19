package com.example.taskscheduler.di

import com.example.taskscheduler.data.entities.*
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.models.*
import dagger.Binds
import dagger.Module

@Module
interface MapperModule {

    @AppScope
    @Binds
    fun bindBoardEntityMapper(impl: BoardEntityToBoardMapper): Mapper<BoardEntity?, Board>

    @AppScope
    @Binds
    fun bindBoardMapper(impl: BoardToBoardEntityMapper): Mapper<Board, BoardEntity>

    @AppScope
    @Binds
    fun bindUserEntityMapper(impl: UserEntityToUserMapper): Mapper<UserEntity?, User>

    @AppScope
    @Binds
    fun bindUserMapper(impl: UserToUserEntityMapper): Mapper<User, UserEntity>

    @AppScope
    @Binds
    fun bindInviteEntityMapper(impl: InviteEntityToInviteMapper): Mapper<InviteEntity?, Invite>

    @AppScope
    @Binds
    fun bindInviteMapper(impl: InviteToInviteEntityMapper): Mapper<Invite, InviteEntity>

    @AppScope
    @Binds
    fun bindNotesListEntityMapper(impl: NotesListEntityToNotesListItemMapper): Mapper<NotesListEntity?, NotesListItem>

    @AppScope
    @Binds
    fun bindNotesListItemMapper(impl: NotesListItemToNotesListEntityMapper): Mapper<NotesListItem, NotesListEntity>

    @AppScope
    @Binds
    fun bindNoteEntityMapper(impl: NoteEntityToNoteMapper): Mapper<NoteEntity?, Note>

    @AppScope
    @Binds
    fun bindNoteMapper(impl: NoteToNoteEntityMapper): Mapper<Note, NoteEntity>

    @AppScope
    @Binds
    fun bindCheckNoteEntityMapper(impl: CheckNoteEntityToCheckNoteItemMapper): Mapper<CheckNoteEntity?, CheckNoteItem>

    @AppScope
    @Binds
    fun bindCheckNoteItemMapper(impl: CheckNoteItemToCheckNoteEntityMapper): Mapper<CheckNoteItem, CheckNoteEntity>
}