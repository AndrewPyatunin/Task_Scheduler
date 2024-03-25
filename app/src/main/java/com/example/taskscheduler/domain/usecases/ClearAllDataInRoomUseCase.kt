package com.example.taskscheduler.domain.usecases

import com.example.taskscheduler.domain.repos.*
import javax.inject.Inject

class ClearAllDataInRoomUseCase @Inject constructor(
    private val inviteRepository: InviteRepository,
    private val boardRepository: BoardRepository,
    private val notesListRepository: NotesListRepository,
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) {

    suspend fun execute() {
        inviteRepository.clearAllInvitesInRoom()
        boardRepository.clearAllBoards()
        notesListRepository.clearAllNotesLists()
        noteRepository.clearAllNotes()
        userRepository.clearAllUsers()
    }
}