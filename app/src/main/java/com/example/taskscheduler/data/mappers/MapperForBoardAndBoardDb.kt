package com.example.taskscheduler.data.mappers

import com.example.taskscheduler.data.entities.BoardEntity
import com.example.taskscheduler.domain.models.Board

class MapperForBoardAndBoardDb {

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

    fun listMap(list: List<BoardEntity>): List<Board> {
        return list.map { map(it) }
    }

    fun mapToDb(board: Board): BoardEntity {
        return BoardEntity(
            id = board.id,
            title = board.title,
            creatorId = board.creatorId,
            backgroundUrl = board.backgroundUrl,
            members = board.members,
            listOfNotesIds = board.listsOfNotesIds
        )
    }

    fun mapListToDb(list: List<Board>): List<BoardEntity> {
        return list.map { mapToDb(it) }
    }
}