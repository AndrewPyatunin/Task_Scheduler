package com.example.taskscheduler.di

import com.example.taskscheduler.data.entities.*
import com.example.taskscheduler.data.mappers.*
import com.example.taskscheduler.domain.models.*
import dagger.Binds
import dagger.Module

@Module
interface MapperModule {

    @Binds
    fun bindBoardEntityMapper(impl: BoardEntityToBoardMapper): Mapper<BoardEntity?, Board>

    @Binds
    fun bindBoardMapper(impl: BoardToBoardEntityMapper): Mapper<Board, BoardEntity>

    @Binds
    fun bindUserEntityMapper(impl: UserEntityToUserMapper): Mapper<UserEntity?, User>

    @Binds
    fun bindUserMapper(impl: UserToUserEntityMapper): Mapper<User, UserEntity>

    @Binds
    fun bindInviteEntityMapper(impl: InviteEntityToInviteMapper): Mapper<InviteEntity?, Invite>

    @Binds
    fun bindInviteMapper(impl: InviteToInviteEntityMapper): Mapper<Invite, InviteEntity>

    @Binds
    fun bindNotesListEntityMapper(impl: NotesListEntityToNotesListItemMapper): Mapper<NotesListEntity?, NotesListItem>

    @Binds
    fun bindNotesListItemMapper(impl: NotesListItemToNotesListEntityMapper): Mapper<NotesListItem, NotesListEntity>

    @Binds
    fun bindNoteEntityMapper(impl: NoteEntityToNoteMapper): Mapper<NoteEntity?, Note>

    @Binds
    fun bindNoteMapper(impl: NoteToNoteEntityMapper): Mapper<Note, NoteEntity>

    @Binds
    fun bindCheckNoteEntityMapper(impl: CheckNoteEntityToCheckNoteItemMapper): Mapper<CheckNoteEntity?, CheckNoteItem>

    @Binds
    fun bindCheckNoteItemMapper(impl: CheckNoteItemToCheckNoteEntityMapper): Mapper<CheckNoteItem, CheckNoteEntity>
}