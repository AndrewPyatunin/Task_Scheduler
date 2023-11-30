package com.example.taskscheduler.data

import com.example.taskscheduler.domain.Board

class MapperForBoardAndBoardDb {

    fun map(boardDb: BoardDb): Board {
        return Board(
            id = boardDb.id,
            title = boardDb.title,
            creatorId = boardDb.creatorId,
            backgroundUrl = boardDb.backgroundUrl,
            members = boardDb.members,
            listsOfNotesIds = boardDb.listOfNotesIds
        )
    }

    fun listMap(list: List<BoardDb>): List<Board> {
        return list.map { map(it) }
    }

    fun mapToDb(board: Board): BoardDb {
        return BoardDb(
            id = board.id,
            title = board.title,
            creatorId = board.creatorId,
            backgroundUrl = board.backgroundUrl,
            members = board.members,
            listOfNotesIds = board.listsOfNotesIds
        )
    }

    fun mapListToDb(list: List<Board>): List<BoardDb> {
        return list.map { mapToDb(it) }
    }
}