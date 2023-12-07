package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.ListOfNotesEntity
import com.example.taskscheduler.domain.models.ListOfNotesItem

class MapperForListsOfNotesAndListsOfNotesDb {
    fun map(listOfNotesEntity: ListOfNotesEntity): ListOfNotesItem {
        return ListOfNotesItem(
            id = listOfNotesEntity.id,
            title = listOfNotesEntity.title,
            creatorId = listOfNotesEntity.creatorId,
            listNotes = listOfNotesEntity.listNotes
        )
    }

    fun mapList(list: List<ListOfNotesEntity>): List<ListOfNotesItem> {
        return list.map { map(it) }
    }

    fun mapToDb(listOfNotesItem: ListOfNotesItem): ListOfNotesEntity {
        return ListOfNotesEntity(
            id = listOfNotesItem.id,
            title = listOfNotesItem.title,
            creatorId = listOfNotesItem.creatorId,
            listNotes = listOfNotesItem.listNotes
        )
    }

    fun mapToDbList(list: List<ListOfNotesItem>): List<ListOfNotesEntity> {
        return list.map { mapToDb(it) }
    }
}