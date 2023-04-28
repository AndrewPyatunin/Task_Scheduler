package com.example.taskscheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.taskscheduler.domain.Board

class BoardListDiffCallback(
    private val oldList: List<Board>,
    private val newList: List<Board>
    ) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldBoard = oldList[oldItemPosition]
        val newBoard = newList[newItemPosition]
        return oldBoard.id == newBoard.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldBoard = oldList[oldItemPosition]
        val newBoard = newList[newItemPosition]
        return oldBoard == newBoard
    }

}

class BoardListAdapter: Adapter<BoardListAdapter.BoardListViewHolder>() {
    var onItemClick:((Board) -> Unit)? = null

    var boards = emptyList<Board>()
    set(newValue) {
        val diffCallback = BoardListDiffCallback(field, newValue)
        val diffResult =DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        field = newValue
    }

    inner class BoardListViewHolder(itemView: View, var textViewBoardName: TextView = itemView.findViewById(R.id.textViewBoardName)) : ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(boards[adapterPosition])
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.board_list_item, parent, false)
        return BoardListViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardListViewHolder, position: Int) {
        val board = boards[position]
        val boardInfo = String.format("%s", board.name)
        holder.textViewBoardName.text = boardInfo
    }

    override fun getItemCount(): Int = boards.size
}