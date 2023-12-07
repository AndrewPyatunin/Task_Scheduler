package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.models.Note

class NoteEntityToNoteMapper {

    fun map(noteEntity: NoteEntity): Note {
        return Note(
            id = noteEntity.id,
            title = noteEntity.title,
            creatorId = noteEntity.creatorId,
            members = noteEntity.members,
            description = noteEntity.description,
            date = noteEntity.date,
            listOfTasks = noteEntity.listOfTasks,
            priority = noteEntity.priority
        )
    }
}