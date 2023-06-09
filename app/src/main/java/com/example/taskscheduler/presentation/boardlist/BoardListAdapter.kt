package com.example.taskscheduler

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.taskscheduler.domain.Board
import com.example.taskscheduler.domain.DiffCallback

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
        val diffCallback = DiffCallback(field, newValue)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        field = newValue
    }

    inner class BoardListViewHolder(
        itemView: View,
        var textViewBoardName: TextView = itemView.findViewById(R.id.textViewBoardName),
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linear_board_item)

    ) : ViewHolder(itemView) {
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
        val boardInfo = String.format("%s", board.title)
        holder.textViewBoardName.text = boardInfo

        if (board.backgroundUrl != "") {
            Glide.with(holder.itemView.context).load(board.backgroundUrl)
                .into(object : CustomTarget<Drawable>() {
                    override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        holder.linearLayout.background = resource
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }

                })
        } else {
            val light = holder.itemView.resources.getColor(R.color.light_font)
            holder.linearLayout.setBackgroundColor(light)
        }
    }

    override fun getItemCount(): Int = boards.size

}