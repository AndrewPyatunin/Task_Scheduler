package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board

class BoardToBoardEntityMapper : Mapper<Board, BoardEntity> {

    override fun map(from: Board): BoardEntity {
        return BoardEntity(
            id = from.id,
            title = from.title,
            creatorId = from.creatorId,
            backgroundUrl = from.backgroundUrl,
            members = from.members,
            listOfNotesIds = from.listsOfNotesIds
        )
    }

}