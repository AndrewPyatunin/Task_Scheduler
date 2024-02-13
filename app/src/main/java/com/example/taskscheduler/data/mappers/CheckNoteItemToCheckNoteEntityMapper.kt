package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.CheckNoteEntity
import com.example.taskscheduler.domain.models.CheckNoteItem

class CheckNoteItemToCheckNoteEntityMapper: Mapper<CheckNoteItem, CheckNoteEntity> {

    override fun map(from: CheckNoteItem): CheckNoteEntity {
        return CheckNoteEntity(
            itemTitle = from.itemTitle,
            isChecked = from.isChecked,
            id = from.id
        )
    }
}