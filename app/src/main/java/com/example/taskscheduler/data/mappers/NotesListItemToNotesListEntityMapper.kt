package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.NotesListEntity
import com.example.taskscheduler.domain.models.NotesListItem

class NotesListItemToNotesListEntityMapper : Mapper<NotesListItem, NotesListEntity> {

    override fun map(from: NotesListItem): NotesListEntity {
        return NotesListEntity(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            listNotes = from.listNotes
        )
    }

}