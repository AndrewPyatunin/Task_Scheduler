package com.example.taskscheduler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.taskscheduler.domain.Board

class NewBoardDiffCallback(
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

    class NewBoardAdapter: RecyclerView.Adapter<NewBoardAdapter.NewBoardViewHolder>() {
        var onItemClick:((Board) -> Unit)? = null

        var boards = ArrayList<Board>()
            set(newValue) {
                val diffCallback = BoardListDiffCallback(field, newValue)
                val diffResult = DiffUtil.calculateDiff(diffCallback)
                diffResult.dispatchUpdatesTo(this)
                field = newValue
            }

        inner class NewBoardViewHolder(itemView: View, var imageViewBackground: ImageView = itemView.findViewById(R.id.imageViewBackground)) : RecyclerView.ViewHolder(itemView) {
            init {
                itemView.setOnClickListener {
                    onItemClick?.invoke(boards[adapterPosition])
                }
            }

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewBoardViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.new_board_background_item, parent, false)
            return NewBoardViewHolder(view)
        }

        override fun getItemCount(): Int = boards.size


        override fun onBindViewHolder(holder: NewBoardViewHolder, position: Int) {
            val board = boards[position]
            val boardInfo = String.format("%s", board.name)
        }
    }