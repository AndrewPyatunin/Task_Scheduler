package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.models.Note

class NoteToNoteEntityMapper {

    fun map(note: Note): NoteEntity {
        return NoteEntity(
            id = note.id,
            title = note.title,
            creatorId = note.creatorId,
            members = note.members,
            description = note.description,
            date = note.date,
            listOfTasks = note.listOfTasks,
            priority = note.priority
        )
    }
}