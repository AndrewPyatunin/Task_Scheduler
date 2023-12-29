package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.CheckNoteEntity
import com.example.taskscheduler.data.entities.NoteEntity
import com.example.taskscheduler.domain.CheckNoteItem
import com.example.taskscheduler.domain.UrgencyOfNote
import com.example.taskscheduler.domain.models.Note

class NoteEntityToNoteMapper(private val mapper: Mapper<CheckNoteEntity?, CheckNoteItem>) :
    Mapper<NoteEntity?, Note> {

    override fun map(from: NoteEntity?): Note {
        return Note(
            id = from?.id ?: "",
            title = from?.title ?: "",
            creatorId = from?.creatorId ?: "",
            members = from?.members ?: emptyList(),
            description = from?.description ?: "",
            date = from?.date ?: "",
            listOfTasks = from?.listOfTasks?.map { mapper.map(it) } ?: emptyList(),
            priority = from?.priority ?: UrgencyOfNote.LOW
        )
    }
}