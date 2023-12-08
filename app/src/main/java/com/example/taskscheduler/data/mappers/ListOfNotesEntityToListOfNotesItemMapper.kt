package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.ListOfNotesEntity
import com.example.taskscheduler.domain.models.ListOfNotesItem

class ListOfNotesEntityToListOfNotesItemMapper : Mapper<ListOfNotesEntity, ListOfNotesItem> {

    override fun map(from: ListOfNotesEntity): ListOfNotesItem {
        return ListOfNotesItem(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            listNotes = from.listNotes
        )
    }

}