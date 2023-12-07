package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.ListOfNotesEntity
import com.example.taskscheduler.domain.models.ListOfNotesItem

class ListOfNotesEntityToListOfNotesItemMapper {

    fun map(listOfNotesEntity: ListOfNotesEntity): ListOfNotesItem {
        return ListOfNotesItem(
            id = listOfNotesEntity.id,
            title = listOfNotesEntity.title,
            creatorId = listOfNotesEntity.creatorId,
            listNotes = listOfNotesEntity.listNotes
        )
    }

}