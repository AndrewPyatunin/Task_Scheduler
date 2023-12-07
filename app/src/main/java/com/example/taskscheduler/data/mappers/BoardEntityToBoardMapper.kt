package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board

class BoardEntityToBoardMapper {
    fun map(boardEntity: BoardEntity): Board {
        return Board(
            id = boardEntity.id,
            title = boardEntity.title,
            creatorId = boardEntity.creatorId,
            backgroundUrl = boardEntity.backgroundUrl,
            members = boardEntity.members,
            listsOfNotesIds = boardEntity.listOfNotesIds
        )
    }
}